package br.edu.vetcare.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public final class Conexao {
    private static String url;
    private static String user;
    private static String password;
    private static boolean pronto;

    private Conexao() {}

    public static Connection abrir() {
        try {
            carregar();
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new DbException("Falha ao abrir conexão com o banco.", e);
        }
    }

    public static synchronized void inicializar() {
        if (pronto) {
            return;
        }
        try (Connection con = abrir(); Statement st = con.createStatement()) {
            if (!tabelaExiste(con, "cliente")) {
                executarScript(con, "/schema.sql");
                executarScript(con, "/seed.sql");
            }
            pronto = true;
        } catch (Exception e) {
            throw new DbException("Falha ao inicializar o banco.", e);
        }
    }

    private static void carregar() throws Exception {
        if (url != null) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Conexao.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new DbException("db.properties não encontrado.");
            }
            props.load(in);
        }
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password", "");
        Class.forName("org.h2.Driver");
    }

    private static boolean tabelaExiste(Connection con, String nome) throws Exception {
        try (ResultSet rs = con.getMetaData().getTables(null, null, nome.toUpperCase(), new String[]{"TABLE"})) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = con.getMetaData().getTables(null, null, nome.toLowerCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static void executarScript(Connection con, String recurso) throws Exception {
        try (InputStream in = Conexao.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new DbException("Script não encontrado: " + recurso);
            }
            String sql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .filter(l -> !l.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));
            for (String comando : sql.split(";")) {
                String c = comando.trim();
                if (!c.isEmpty()) {
                    try (Statement st = con.createStatement()) {
                        st.execute(c);
                    }
                }
            }
        }
    }
}
