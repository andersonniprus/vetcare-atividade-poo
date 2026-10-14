package br.edu.vetcare.view;

import br.edu.vetcare.controller.ProcedimentoController;
import br.edu.vetcare.model.Procedimento;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;

public class ProcedimentoPanel extends JPanel {
    private final ProcedimentoController controller = new ProcedimentoController();
    private final DefaultTableModel modelo = Ui.modelo("Id", "Nome", "Descrição", "Valor");
    private final JTable tabela = new JTable(modelo);
    private final JTextField busca = Ui.campo();
    private final JTextField nome = Ui.campo();
    private final JTextField descricao = Ui.campo();
    private final JTextField valor = Ui.campo();
    private int idAtual;

    public ProcedimentoPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        JButton pesquisar = Ui.botao("Pesquisar");
        pesquisar.addActionListener(e -> carregar());
        add(Ui.barra(busca, pesquisar), BorderLayout.NORTH);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                int row = tabela.getSelectedRow();
                idAtual = (int) modelo.getValueAt(row, 0);
                nome.setText(String.valueOf(modelo.getValueAt(row, 1)));
                descricao.setText(txt(modelo.getValueAt(row, 2)));
                valor.setText(String.valueOf(modelo.getValueAt(row, 3)));
            }
        });
        add(Ui.tabela(tabela), BorderLayout.CENTER);
        JPanel form = new JPanel(new GridLayout(1, 3, 8, 8));
        form.setOpaque(false);
        form.add(Ui.rotulo("Nome", nome));
        form.add(Ui.rotulo("Descrição", descricao));
        form.add(Ui.rotulo("Valor", valor));
        JButton novo = Ui.botao("Novo");
        JButton salvar = Ui.botao("Salvar");
        JButton inativar = Ui.botao("Inativar");
        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        inativar.addActionListener(e -> {
            if (idAtual == 0) {
                Ui.erro(this, "Selecione um procedimento.");
                return;
            }
            if (Ui.confirmar(this, "Inativar este procedimento?")) {
                controller.inativar(idAtual);
                limpar();
                carregar();
            }
        });
        JPanel sul = new JPanel(new BorderLayout());
        sul.setOpaque(false);
        sul.add(form, BorderLayout.CENTER);
        sul.add(Ui.barra(novo, salvar, inativar), BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);
        carregar();
    }

    private void carregar() {
        modelo.setRowCount(0);
        for (Procedimento p : controller.listar(busca.getText())) {
            modelo.addRow(new Object[]{p.getId(), p.getNome(), p.getDescricao(), p.getValor()});
        }
    }

    private void salvar() {
        try {
            Procedimento p = new Procedimento();
            p.setId(idAtual);
            p.setNome(nome.getText().trim());
            p.setDescricao(descricao.getText().trim());
            p.setValor(new BigDecimal(valor.getText().trim().replace(',', '.')));
            controller.salvar(p);
            Ui.info(this, "Procedimento salvo.");
            limpar();
            carregar();
        } catch (NumberFormatException ex) {
            Ui.erro(this, "Valor inválido.");
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void limpar() {
        idAtual = 0;
        nome.setText("");
        descricao.setText("");
        valor.setText("");
        tabela.clearSelection();
    }

    private String txt(Object o) {
        return o == null ? "" : o.toString();
    }
}
