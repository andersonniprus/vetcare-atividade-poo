package br.edu.vetcare.view;

import br.edu.vetcare.controller.AnimalController;
import br.edu.vetcare.controller.ClienteController;
import br.edu.vetcare.model.Animal;
import br.edu.vetcare.model.Cliente;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AnimalPanel extends JPanel {
    private final AnimalController controller = new AnimalController();
    private final ClienteController clientes = new ClienteController();
    private final DefaultTableModel modelo = Ui.modelo("Id", "Nome", "Espécie", "Raça", "Sexo", "Tutor id", "Peso", "Nasc.");
    private final JTable tabela = new JTable(modelo);
    private final JTextField busca = Ui.campo();
    private final JTextField nome = Ui.campo();
    private final JTextField especie = Ui.campo();
    private final JTextField raca = Ui.campo();
    private final JComboBox<String> sexo = new JComboBox<>(new String[]{"M", "F"});
    private final JComboBox<Cliente> tutor = new JComboBox<>();
    private final JTextField peso = Ui.campo();
    private final JTextField nascimento = Ui.campo();
    private int idAtual;

    public AnimalPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        JButton pesquisar = Ui.botao("Pesquisar");
        pesquisar.addActionListener(e -> carregar());
        add(Ui.barra(busca, pesquisar), BorderLayout.NORTH);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencher();
            }
        });
        add(Ui.tabela(tabela), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setOpaque(false);
        form.add(Ui.rotulo("Nome", nome));
        form.add(Ui.rotulo("Tutor", tutor));
        form.add(Ui.rotulo("Espécie", especie));
        form.add(Ui.rotulo("Raça", raca));
        form.add(Ui.rotulo("Sexo", sexo));
        form.add(Ui.rotulo("Peso kg", peso));
        form.add(Ui.rotulo("Nascimento (aaaa-mm-dd)", nascimento));

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
        carregarTutores();
        carregar();
    }

    private void carregarTutores() {
        tutor.removeAllItems();
        for (Cliente c : clientes.listar("")) {
            tutor.addItem(c);
        }
        tutor.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            javax.swing.JLabel l = new javax.swing.JLabel(value == null ? "" : value.getNome());
            if (isSelected) {
                l.setBackground(list.getSelectionBackground());
                l.setOpaque(true);
            }
            return l;
        });
    }

    private void carregar() {
        modelo.setRowCount(0);
        for (Animal a : controller.listar(busca.getText())) {
            modelo.addRow(new Object[]{a.getId(), a.getNome(), a.getEspecie(), a.getRaca(), a.getSexo(),
                    a.getClienteId(), a.getPesoKg(), a.getDataNascimento()});
        }
    }

    private void preencher() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        idAtual = (int) modelo.getValueAt(row, 0);
        nome.setText(String.valueOf(modelo.getValueAt(row, 1)));
        especie.setText(String.valueOf(modelo.getValueAt(row, 2)));
        raca.setText(modelo.getValueAt(row, 3) == null ? "" : String.valueOf(modelo.getValueAt(row, 3)));
        sexo.setSelectedItem(String.valueOf(modelo.getValueAt(row, 4)));
        int clienteId = (int) modelo.getValueAt(row, 5);
        for (int i = 0; i < tutor.getItemCount(); i++) {
            if (tutor.getItemAt(i).getId() == clienteId) {
                tutor.setSelectedIndex(i);
                break;
            }
        }
        peso.setText(modelo.getValueAt(row, 6) == null ? "" : String.valueOf(modelo.getValueAt(row, 6)));
        nascimento.setText(modelo.getValueAt(row, 7) == null ? "" : String.valueOf(modelo.getValueAt(row, 7)));
    }

    private void salvar() {
        try {
            Cliente c = (Cliente) tutor.getSelectedItem();
            Animal a = new Animal();
            a.setId(idAtual);
            a.setNome(nome.getText().trim());
            a.setEspecie(especie.getText().trim());
            a.setRaca(raca.getText().trim());
            a.setSexo(String.valueOf(sexo.getSelectedItem()).charAt(0));
            a.setClienteId(c == null ? 0 : c.getId());
            if (!peso.getText().isBlank()) {
                a.setPesoKg(new BigDecimal(peso.getText().trim().replace(',', '.')));
            }
            if (!nascimento.getText().isBlank()) {
                a.setDataNascimento(LocalDate.parse(nascimento.getText().trim()));
            }
            controller.salvar(a);
            Ui.info(this, "Animal salvo.");
            limpar();
            carregar();
        } catch (DateTimeParseException ex) {
            Ui.erro(this, "Data inválida. Use aaaa-mm-dd.");
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void inativar() {
        if (idAtual == 0) {
            Ui.erro(this, "Selecione um animal.");
            return;
        }
        if (Ui.confirmar(this, "Inativar este animal?")) {
            controller.inativar(idAtual);
            limpar();
            carregar();
        }
    }

    private void limpar() {
        idAtual = 0;
        nome.setText("");
        especie.setText("");
        raca.setText("");
        peso.setText("");
        nascimento.setText("");
        tabela.clearSelection();
    }
}
