package br.edu.vetcare.dao;

import br.edu.vetcare.model.Procedimento;
import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProcedimentoDao {
    public List<Procedimento> listar(String filtro) {
        String sql = "SELECT * FROM procedimento WHERE ativo = TRUE AND nome LIKE ? ORDER BY nome";
        List<Procedimento> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
            return lista;
        } catch (Exception e) {
            throw new DbException("Erro ao listar procedimentos.", e);
        }
    }

    public Procedimento buscar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM procedimento WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (Exception e) {
            throw new DbException("Erro ao buscar procedimento.", e);
        }
    }

    public void salvar(Procedimento p) {
        if (p.getId() == 0) {
            String sql = "INSERT INTO procedimento (nome, descricao, valor, ativo) VALUES (?, ?, ?, TRUE)";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(ps, p);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setId(keys.getInt(1));
                    }
                }
            } catch (Exception e) {
                throw new DbException("Erro ao incluir procedimento.", e);
            }
        } else {
            String sql = "UPDATE procedimento SET nome=?, descricao=?, valor=? WHERE id=?";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
                preencher(ps, p);
                ps.setInt(4, p.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DbException("Erro ao atualizar procedimento.", e);
            }
        }
    }

    public void inativar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE procedimento SET ativo = FALSE WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DbException("Erro ao inativar procedimento.", e);
        }
    }

    private void preencher(PreparedStatement ps, Procedimento p) throws Exception {
        ps.setString(1, p.getNome());
        ps.setString(2, p.getDescricao());
        ps.setBigDecimal(3, p.getValor());
    }

    private Procedimento map(ResultSet rs) throws Exception {
        Procedimento p = new Procedimento();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setValor(rs.getBigDecimal("valor"));
        p.setAtivo(rs.getBoolean("ativo"));
        return p;
    }
}
