package br.edu.vetcare.view;

import br.edu.vetcare.controller.ClienteController;
import br.edu.vetcare.model.Cliente;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ClientePanel extends JPanel {
    private final ClienteController controller = new ClienteController();
    private final DefaultTableModel modelo = Ui.modelo("Id", "Nome", "CPF", "Telefone", "E-mail", "Endereço");
    private final JTable tabela = new JTable(modelo);
    private final JTextField busca = Ui.campo();
    private final JTextField nome = Ui.campo();
    private final JTextField cpf = Ui.campo();
    private final JTextField telefone = Ui.campo();
    private final JTextField email = Ui.campo();
    private final JTextField endereco = Ui.campo();
    private int idAtual;

    public ClientePanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JButton pesquisar = Ui.botao("Pesquisar");
        pesquisar.addActionListener(e -> carregar());
        add(Ui.barra(busca, pesquisar), BorderLayout.NORTH);

        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherSelecionado();
            }
        });
        add(Ui.tabela(tabela), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 3, 8, 8));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(8, 0, 0, 0));
        form.add(Ui.rotulo("Nome", nome));
        form.add(Ui.rotulo("CPF", cpf));
        form.add(Ui.rotulo("Telefone", telefone));
        form.add(Ui.rotulo("E-mail", email));
        form.add(Ui.rotulo("Endereço", endereco));

        JButton novo = Ui.botao("Novo");
        JButton salvar = Ui.botao("Salvar");
        JButton inativar = Ui.botao("Inativar");
        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        inativar.addActionListener(e -> inativar());
        JPanel sul = new JPanel(new BorderLayout());
        sul.setOpaque(false);
        sul.add(form, BorderLayout.CENTER);
        sul.add(Ui.barra(novo, salvar, inativar), BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);
        carregar();
    }

    private void carregar() {
        modelo.setRowCount(0);
        for (Cliente c : controller.listar(busca.getText())) {
            modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getTelefone(), c.getEmail(), c.getEndereco()});
        }
    }

    private void preencherSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        idAtual = (int) modelo.getValueAt(row, 0);
        nome.setText(str(modelo.getValueAt(row, 1)));
        cpf.setText(str(modelo.getValueAt(row, 2)));
        telefone.setText(str(modelo.getValueAt(row, 3)));
        email.setText(str(modelo.getValueAt(row, 4)));
        endereco.setText(str(modelo.getValueAt(row, 5)));
    }

    private void salvar() {
        try {
            Cliente c = new Cliente();
            c.setId(idAtual);
            c.setNome(nome.getText().trim());
            c.setCpf(cpf.getText().trim());
            c.setTelefone(telefone.getText().trim());
            c.setEmail(email.getText().trim());
            c.setEndereco(endereco.getText().trim());
            controller.salvar(c);
            Ui.info(this, "Cliente salvo.");
            limpar();
            carregar();
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void inativar() {
        if (idAtual == 0) {
            Ui.erro(this, "Selecione um cliente.");
            return;
        }
        if (Ui.confirmar(this, "Inativar este cliente?")) {
            controller.inativar(idAtual);
            limpar();
            carregar();
        }
    }

    private void limpar() {
        idAtual = 0;
        nome.setText("");
        cpf.setText("");
        telefone.setText("");
        email.setText("");
        endereco.setText("");
        tabela.clearSelection();
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
