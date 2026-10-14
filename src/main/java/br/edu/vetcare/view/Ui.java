package br.edu.vetcare.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public final class Ui {
    public static final Color FUNDO = new Color(244, 247, 250);
    public static final Color BARRA = new Color(22, 56, 74);

    private Ui() {}

    public static JButton botao(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        return b;
    }

    public static JTextField campo() {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(160, 28));
        return t;
    }

    public static JPanel rotulo(String texto, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(texto);
        l.setForeground(new Color(70, 90, 104));
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public static JPanel barra(JComponent... componentes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);
        for (JComponent c : componentes) {
            p.add(c);
        }
        return p;
    }

    public static JScrollPane tabela(JTable table) {
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(210, 230, 238));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 212, 220)));
        return sp;
    }

    public static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void erro(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, msg, "VetCare", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, msg, "VetCare", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component pai, String msg) {
        return JOptionPane.showConfirmDialog(pai, msg, "VetCare", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static JPanel cartao(String titulo, String valor) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 228)),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel v = new JLabel(valor);
        v.setFont(v.getFont().deriveFont(22f));
        v.setForeground(BARRA);
        JLabel t = new JLabel(titulo);
        t.setForeground(new Color(90, 110, 122));
        p.add(v);
        p.add(t);
        return p;
    }
}
