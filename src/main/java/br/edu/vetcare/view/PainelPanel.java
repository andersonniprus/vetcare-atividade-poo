package br.edu.vetcare.view;

import br.edu.vetcare.dao.AnimalDao;
import br.edu.vetcare.dao.ClienteDao;
import br.edu.vetcare.dao.ConsultaDao;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PainelPanel extends JPanel {
    private final JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));

    public PainelPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Ui.FUNDO);
        setBorder(new javax.swing.border.EmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("Painel do dia");
        titulo.setFont(titulo.getFont().deriveFont(18f));
        titulo.setForeground(Ui.BARRA);
        add(titulo, BorderLayout.NORTH);
        cards.setOpaque(false);
        add(cards, BorderLayout.CENTER);
        recarregar();
    }

    public void recarregar() {
        cards.removeAll();
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        int consultas = 0;
        int animais = 0;
        int clientes = 0;
        BigDecimal fat = BigDecimal.ZERO;
        try {
            consultas = new ConsultaDao().contarHoje();
            animais = new AnimalDao().contarAtivos();
            clientes = new ClienteDao().contarAtivos();
            fat = new ConsultaDao().faturadoHoje();
        } catch (RuntimeException ignored) {
            // painel mostra zero se o banco falhar
        }
        cards.add(Ui.cartao("Consultas hoje", String.valueOf(consultas)));
        cards.add(Ui.cartao("Animais ativos", String.valueOf(animais)));
        cards.add(Ui.cartao("Tutores ativos", String.valueOf(clientes)));
        cards.add(Ui.cartao("Faturado hoje", moeda.format(fat)));
        cards.revalidate();
        cards.repaint();
    }
}
