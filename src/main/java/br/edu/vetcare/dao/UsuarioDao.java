package br.edu.vetcare.dao;

import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.util.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDao {
    public String autenticar(String login, String senha) {
        String sql = "SELECT nome FROM usuario WHERE login = ? AND senha = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("nome") : null;
            }
        } catch (Exception e) {
            throw new DbException("Erro ao autenticar.", e);
        }
    }
}
