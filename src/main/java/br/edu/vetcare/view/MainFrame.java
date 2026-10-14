package br.edu.vetcare.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;

public class MainFrame extends JFrame {
    private final PainelPanel painel = new PainelPanel();
    private final ConsultaPanel consultas = new ConsultaPanel();

    public MainFrame(String usuario) {
        super("VetCare — " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JLabel barra = new JLabel("  VetCare  ·  logado como " + usuario);
        barra.setOpaque(true);
        barra.setBackground(Ui.BARRA);
        barra.setForeground(Color.WHITE);
        barra.setBorder(new javax.swing.border.EmptyBorder(10, 8, 10, 8));

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Painel", painel);
        abas.addTab("Clientes", new ClientePanel());
        abas.addTab("Animais", new AnimalPanel());
        abas.addTab("Veterinários", new VeterinarioPanel());
        abas.addTab("Procedimentos", new ProcedimentoPanel());
        abas.addTab("Consultas", consultas);
        abas.addChangeListener(e -> {
            if (abas.getSelectedIndex() == 0) {
                painel.recarregar();
            }
            if (abas.getSelectedComponent() == consultas) {
                consultas.recarregarCombos();
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.add(barra, BorderLayout.NORTH);
        root.add(abas, BorderLayout.CENTER);
        setContentPane(root);
    }
}
