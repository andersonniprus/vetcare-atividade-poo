package br.edu.vetcare.dao;

import br.edu.vetcare.model.Cliente;
import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {
    public List<Cliente> listar(String filtro) {
        String sql = "SELECT * FROM cliente WHERE ativo = TRUE AND (nome LIKE ? OR cpf LIKE ?) ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        String like = "%" + (filtro == null ? "" : filtro.trim()) + "%";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
            return lista;
        } catch (Exception e) {
            throw new DbException("Erro ao listar clientes.", e);
        }
    }

    public Cliente buscar(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (Exception e) {
            throw new DbException("Erro ao buscar cliente.", e);
        }
    }

    public void salvar(Cliente c) {
        if (c.getId() == 0) {
            String sql = "INSERT INTO cliente (nome, cpf, telefone, email, endereco, ativo) VALUES (?, ?, ?, ?, ?, TRUE)";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(ps, c);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setId(keys.getInt(1));
                    }
                }
            } catch (Exception e) {
                throw new DbException("Erro ao incluir cliente. Verifique se o CPF já existe.", e);
            }
        } else {
            String sql = "UPDATE cliente SET nome=?, cpf=?, telefone=?, email=?, endereco=? WHERE id=?";
            try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
                preencher(ps, c);
                ps.setInt(6, c.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DbException("Erro ao atualizar cliente.", e);
            }
        }
    }

    public void inativar(int id) {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE cliente SET ativo = FALSE WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DbException("Erro ao inativar cliente.", e);
        }
    }

    public int contarAtivos() {
        return contar("SELECT COUNT(*) FROM cliente WHERE ativo = TRUE");
    }

    private int contar(String sql) {
        try (Connection con = Conexao.abrir(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new DbException("Erro ao contar registros.", e);
        }
    }

    private void preencher(PreparedStatement ps, Cliente c) throws Exception {
        ps.setString(1, c.getNome());
        ps.setString(2, c.getCpf());
        ps.setString(3, c.getTelefone());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getEndereco());
    }

    private Cliente map(ResultSet rs) throws Exception {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        c.setAtivo(rs.getBoolean("ativo"));
        return c;
    }
}
