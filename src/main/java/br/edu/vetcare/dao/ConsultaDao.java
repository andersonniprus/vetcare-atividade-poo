package br.edu.vetcare.dao;

import br.edu.vetcare.model.Consulta;
import br.edu.vetcare.model.ItemConsulta;
import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDao {
    public static class Linha {
        public Consulta consulta;
        public String animal;
        public String tutor;
        public String veterinario;
    }

    public List<Linha> listar(String filtro) {
        String sql = """
                SELECT c.*, a.nome AS animal, cl.nome AS tutor, v.nome AS veterinario
                FROM consulta c
                JOIN animal a ON a.id = c.animal_id
                JOIN cliente cl ON cl.id = a.cliente_id
                JOIN veterinario v ON v.id = c.veterinario_id
                WHERE a.nome LIKE ? OR cl.nome LIKE ? OR v.nome LIKE ? OR c.status LIKE ?
                ORDER BY c.data_hora DESC
                """;
        List<Linha> lista = new ArrayList<>();
        String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Linha linha = new Linha();
                    linha.consulta = map(rs);
                    linha.animal = rs.getString("animal");
                    linha.tutor = rs.getString("tutor");
                    linha.veterinario = rs.getString("veterinario");
                    lista.add(linha);
                }
            }
            return lista;
        } catch (Exception e) {
            throw new DbException("Erro ao listar consultas.", e);
        }
    }

    public List<ItemConsulta> itens(int consultaId) {
        String sql = "SELECT * FROM item_consulta WHERE consulta_id = ? ORDER BY id";
        List<ItemConsulta> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, consultaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemConsulta item = new ItemConsulta();
                    item.setId(rs.getInt("id"));
                    item.setConsultaId(rs.getInt("consulta_id"));
                    item.setProcedimentoId(rs.getInt("procedimento_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setValorUnitario(rs.getBigDecimal("valor_unitario"));
                    lista.add(item);
                }
            }
            return lista;
        } catch (Exception e) {
            throw new DbException("Erro ao listar itens da consulta.", e);
        }
    }

    public void salvar(Consulta consulta, List<ItemConsulta> itens) {
        String insert = "INSERT INTO consulta (animal_id, veterinario_id, data_hora, motivo, diagnostico, status, valor_total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String update = "UPDATE consulta SET animal_id=?, veterinario_id=?, data_hora=?, motivo=?, diagnostico=?, status=?, valor_total=? WHERE id=?";
        try (Connection con = Conexao.abrir()) {
            con.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                for (ItemConsulta item : itens) {
                    total = total.add(item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                }
                consulta.setValorTotal(total);
                if (consulta.getId() == 0) {
                    try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                        preencher(ps, consulta);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) {
                                consulta.setId(keys.getInt(1));
                            }
                        }
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(update)) {
                        preencher(ps, consulta);
                        ps.setInt(8, consulta.getId());
                        ps.executeUpdate();
                    }
                    try (PreparedStatement del = con.prepareStatement("DELETE FROM item_consulta WHERE consulta_id = ?")) {
                        del.setInt(1, consulta.getId());
                        del.executeUpdate();
                    }
                }
                String itemSql = "INSERT INTO item_consulta (consulta_id, procedimento_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    for (ItemConsulta item : itens) {
                        ps.setInt(1, consulta.getId());
                        ps.setInt(2, item.getProcedimentoId());
                        ps.setInt(3, item.getQuantidade());
                        ps.setBigDecimal(4, item.getValorUnitario());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            throw new DbException("Erro ao salvar consulta.", e);
        }
    }

    public void cancelar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE consulta SET status = 'CANCELADA' WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DbException("Erro ao cancelar consulta.", e);
        }
    }

    public int contarHoje() {
        String sql = "SELECT COUNT(*) FROM consulta WHERE CAST(data_hora AS DATE) = CURRENT_DATE AND status <> 'CANCELADA'";
        try (Connection con = Conexao.abrir(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new DbException("Erro ao contar consultas do dia.", e);
        }
    }

    public BigDecimal faturadoHoje() {
        String sql = "SELECT COALESCE(SUM(valor_total), 0) FROM consulta WHERE CAST(data_hora AS DATE) = CURRENT_DATE AND status = 'CONCLUIDA'";
        try (Connection con = Conexao.abrir(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (Exception e) {
            throw new DbException("Erro ao somar faturamento.", e);
        }
    }

    private void preencher(PreparedStatement ps, Consulta c) throws Exception {
        ps.setInt(1, c.getAnimalId());
        ps.setInt(2, c.getVeterinarioId());
        ps.setTimestamp(3, Timestamp.valueOf(c.getDataHora()));
        ps.setString(4, c.getMotivo());
        ps.setString(5, c.getDiagnostico());
        ps.setString(6, c.getStatus());
        ps.setBigDecimal(7, c.getValorTotal());
    }

    private Consulta map(ResultSet rs) throws Exception {
        Consulta c = new Consulta();
        c.setId(rs.getInt("id"));
        c.setAnimalId(rs.getInt("animal_id"));
        c.setVeterinarioId(rs.getInt("veterinario_id"));
        Timestamp ts = rs.getTimestamp("data_hora");
        c.setDataHora(ts == null ? null : ts.toLocalDateTime());
        c.setMotivo(rs.getString("motivo"));
        c.setDiagnostico(rs.getString("diagnostico"));
        c.setStatus(rs.getString("status"));
        c.setValorTotal(rs.getBigDecimal("valor_total"));
        return c;
    }
}
