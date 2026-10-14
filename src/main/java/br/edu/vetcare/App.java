package br.edu.vetcare;

import br.edu.vetcare.util.Conexao;
import br.edu.vetcare.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // mantém o look and feel padrão
        }
        Conexao.inicializar();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
