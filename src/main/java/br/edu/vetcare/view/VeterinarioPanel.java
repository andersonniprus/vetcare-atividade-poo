package br.edu.vetcare.view;

import br.edu.vetcare.controller.VeterinarioController;
import br.edu.vetcare.model.Veterinario;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class VeterinarioPanel extends JPanel {
    private final VeterinarioController controller = new VeterinarioController();
    private final DefaultTableModel modelo = Ui.modelo("Id", "Nome", "CRMV", "Especialidade", "Telefone", "E-mail");
    private final JTable tabela = new JTable(modelo);
    private final JTextField busca = Ui.campo();
    private final JTextField nome = Ui.campo();
    private final JTextField crmv = Ui.campo();
    private final JTextField especialidade = Ui.campo();
    private final JTextField telefone = Ui.campo();
    private final JTextField email = Ui.campo();
    private int idAtual;

    public VeterinarioPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        JButton pesquisar = Ui.botao("Pesquisar");
        pesquisar.addActionListener(e -> carregar());
        add(Ui.barra(busca, pesquisar), BorderLayout.NORTH);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabela.getSelectedRow();
                if (row >= 0) {
                    idAtual = (int) modelo.getValueAt(row, 0);
                    nome.setText(String.valueOf(modelo.getValueAt(row, 1)));
                    crmv.setText(String.valueOf(modelo.getValueAt(row, 2)));
                    especialidade.setText(String.valueOf(modelo.getValueAt(row, 3)));
                    telefone.setText(txt(modelo.getValueAt(row, 4)));
                    email.setText(txt(modelo.getValueAt(row, 5)));
                }
            }
        });
        add(Ui.tabela(tabela), BorderLayout.CENTER);
        JPanel form = new JPanel(new GridLayout(1, 5, 8, 8));
        form.setOpaque(false);
        form.add(Ui.rotulo("Nome", nome));
        form.add(Ui.rotulo("CRMV", crmv));
        form.add(Ui.rotulo("Especialidade", especialidade));
        form.add(Ui.rotulo("Telefone", telefone));
        form.add(Ui.rotulo("E-mail", email));
        JButton novo = Ui.botao("Novo");
        JButton salvar = Ui.botao("Salvar");
        JButton inativar = Ui.botao("Inativar");
        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        inativar.addActionListener(e -> {
            if (idAtual == 0) {
                Ui.erro(this, "Selecione um veterinário.");
                return;
            }
            if (Ui.confirmar(this, "Inativar este veterinário?")) {
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
        for (Veterinario v : controller.listar(busca.getText())) {
            modelo.addRow(new Object[]{v.getId(), v.getNome(), v.getCrmv(), v.getEspecialidade(), v.getTelefone(), v.getEmail()});
        }
    }

    private void salvar() {
        try {
            Veterinario v = new Veterinario();
            v.setId(idAtual);
            v.setNome(nome.getText().trim());
            v.setCrmv(crmv.getText().trim());
            v.setEspecialidade(especialidade.getText().trim());
            v.setTelefone(telefone.getText().trim());
            v.setEmail(email.getText().trim());
            controller.salvar(v);
            Ui.info(this, "Veterinário salvo.");
            limpar();
            carregar();
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void limpar() {
        idAtual = 0;
        nome.setText("");
        crmv.setText("");
        especialidade.setText("");
        telefone.setText("");
        email.setText("");
        tabela.clearSelection();
    }

    private String txt(Object o) {
        return o == null ? "" : o.toString();
    }
}
