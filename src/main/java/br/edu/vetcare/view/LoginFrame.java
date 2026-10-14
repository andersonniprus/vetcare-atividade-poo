package br.edu.vetcare.view;

import br.edu.vetcare.dao.UsuarioDao;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final JTextField login = Ui.campo();
    private final JPasswordField senha = new JPasswordField();
    private final UsuarioDao usuarios = new UsuarioDao();

    public LoginFrame() {
        super("VetCare — Acesso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        JLabel titulo = new JLabel("VetCare", JLabel.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(Ui.BARRA);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(20f));
        titulo.setBorder(new javax.swing.border.EmptyBorder(16, 0, 16, 0));

        JPanel form = new JPanel(new GridLayout(2, 1, 8, 8));
        form.setBorder(new javax.swing.border.EmptyBorder(16, 24, 8, 24));
        form.setOpaque(false);
        form.add(Ui.rotulo("Usuário", login));
        form.add(Ui.rotulo("Senha", senha));
        JButton entrar = Ui.botao("Entrar");
        entrar.addActionListener(e -> autenticar());
        senha.addActionListener(e -> autenticar());
        login.setText("admin");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Ui.FUNDO);
        root.add(titulo, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(Ui.barra(entrar), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void autenticar() {
        try {
            String nome = usuarios.autenticar(login.getText().trim(), new String(senha.getPassword()));
            if (nome == null) {
                Ui.erro(this, "Usuário ou senha inválidos.");
                return;
            }
            MainFrame main = new MainFrame(nome);
            main.setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }
}
