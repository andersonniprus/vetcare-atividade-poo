package br.edu.vetcare.dao;

import br.edu.vetcare.model.Veterinario;
import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDao {
    public List<Veterinario> listar(String filtro) {
        String sql = "SELECT * FROM veterinario WHERE ativo = TRUE AND (nome LIKE ? OR crmv LIKE ? OR especialidade LIKE ?) ORDER BY nome";
        List<Veterinario> lista = new ArrayList<>();
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
            throw new DbException("Erro ao listar veterinários.", e);
        }
    }

    public void salvar(Veterinario v) {
        if (v.getId() == 0) {
            String sql = "INSERT INTO veterinario (nome, crmv, especialidade, telefone, email, ativo) VALUES (?, ?, ?, ?, ?, TRUE)";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(ps, v);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        v.setId(keys.getInt(1));
                    }
                }
            } catch (Exception e) {
                throw new DbException("Erro ao incluir veterinário. Verifique se o CRMV já existe.", e);
            }
        } else {
            String sql = "UPDATE veterinario SET nome=?, crmv=?, especialidade=?, telefone=?, email=? WHERE id=?";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
                preencher(ps, v);
                ps.setInt(6, v.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DbException("Erro ao atualizar veterinário.", e);
            }
        }
    }

    public void inativar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE veterinario SET ativo = FALSE WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DbException("Erro ao inativar veterinário.", e);
        }
    }

    private void preencher(PreparedStatement ps, Veterinario v) throws Exception {
        ps.setString(1, v.getNome());
        ps.setString(2, v.getCrmv());
        ps.setString(3, v.getEspecialidade());
        ps.setString(4, v.getTelefone());
        ps.setString(5, v.getEmail());
    }

    private Veterinario map(ResultSet rs) throws Exception {
        Veterinario v = new Veterinario();
        v.setId(rs.getInt("id"));
        v.setNome(rs.getString("nome"));
        v.setCrmv(rs.getString("crmv"));
        v.setEspecialidade(rs.getString("especialidade"));
        v.setTelefone(rs.getString("telefone"));
        v.setEmail(rs.getString("email"));
        v.setAtivo(rs.getBoolean("ativo"));
        return v;
    }
}
