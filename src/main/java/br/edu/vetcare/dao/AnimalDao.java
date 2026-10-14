package br.edu.vetcare.dao;

import br.edu.vetcare.model.Animal;
import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalDao {
    public List<Animal> listar(String filtro) {
        String sql = """
                SELECT a.* FROM animal a
                JOIN cliente c ON c.id = a.cliente_id
                WHERE a.ativo = TRUE AND (a.nome LIKE ? OR c.nome LIKE ? OR a.especie LIKE ?)
                ORDER BY a.nome
                """;
        List<Animal> lista = new ArrayList<>();
        String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
            return lista;
        } catch (Exception e) {
            throw new DbException("Erro ao listar animais.", e);
        }
    }

    public void salvar(Animal a) {
        if (a.getId() == 0) {
            String sql = "INSERT INTO animal (cliente_id, nome, especie, raca, sexo, data_nascimento, peso_kg, observacoes, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(ps, a);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setId(keys.getInt(1));
                    }
                }
            } catch (Exception e) {
                throw new DbException("Erro ao incluir animal.", e);
            }
        } else {
            String sql = "UPDATE animal SET cliente_id=?, nome=?, especie=?, raca=?, sexo=?, data_nascimento=?, peso_kg=?, observacoes=? WHERE id=?";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
                preencher(ps, a);
                ps.setInt(9, a.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DbException("Erro ao atualizar animal.", e);
            }
        }
    }

    public void inativar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE animal SET ativo = FALSE WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DbException("Erro ao inativar animal.", e);
        }
    }

    public int contarAtivos() {
        try (Connection con = Conexao.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM animal WHERE ativo = TRUE")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new DbException("Erro ao contar animais.", e);
        }
    }

    private void preencher(PreparedStatement ps, Animal a) throws Exception {
        ps.setInt(1, a.getClienteId());
        ps.setString(2, a.getNome());
        ps.setString(3, a.getEspecie());
        ps.setString(4, a.getRaca());
        ps.setString(5, String.valueOf(a.getSexo()));
        if (a.getDataNascimento() == null) {
            ps.setNull(6, java.sql.Types.DATE);
        } else {
            ps.setDate(6, Date.valueOf(a.getDataNascimento()));
        }
        if (a.getPesoKg() == null) {
            ps.setNull(7, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(7, a.getPesoKg());
        }
        ps.setString(8, a.getObservacoes());
    }

    private Animal map(ResultSet rs) throws Exception {
        Animal a = new Animal();
        a.setId(rs.getInt("id"));
        a.setClienteId(rs.getInt("cliente_id"));
        a.setNome(rs.getString("nome"));
        a.setEspecie(rs.getString("especie"));
        a.setRaca(rs.getString("raca"));
        String sexo = rs.getString("sexo");
        a.setSexo(sexo == null || sexo.isEmpty() ? 'M' : sexo.charAt(0));
        Date nasc = rs.getDate("data_nascimento");
        a.setDataNascimento(nasc == null ? null : nasc.toLocalDate());
        a.setPesoKg(rs.getBigDecimal("peso_kg"));
        a.setObservacoes(rs.getString("observacoes"));
        a.setAtivo(rs.getBoolean("ativo"));
        return a;
    }
}
