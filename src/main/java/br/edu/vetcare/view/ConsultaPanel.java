package br.edu.vetcare.view;

import br.edu.vetcare.controller.AnimalController;
import br.edu.vetcare.controller.ConsultaController;
import br.edu.vetcare.controller.ProcedimentoController;
import br.edu.vetcare.controller.VeterinarioController;
import br.edu.vetcare.dao.ConsultaDao;
import br.edu.vetcare.model.Animal;
import br.edu.vetcare.model.Consulta;
import br.edu.vetcare.model.ItemConsulta;
import br.edu.vetcare.model.Procedimento;
import br.edu.vetcare.model.Veterinario;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ConsultaPanel extends JPanel {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ConsultaController controller = new ConsultaController();
    private final DefaultTableModel modelo = Ui.modelo("Id", "Data", "Animal", "Tutor", "Veterinário", "Status", "Total");
    private final JTable tabela = new JTable(modelo);
    private final DefaultTableModel modeloItens = Ui.modelo("Proc id", "Procedimento", "Qtd", "Valor unit.");
    private final JTable itensTabela = new JTable(modeloItens);
    private final JTextField busca = Ui.campo();
    private final JComboBox<Animal> animal = new JComboBox<>();
    private final JComboBox<Veterinario> veterinario = new JComboBox<>();
    private final JComboBox<Procedimento> procedimento = new JComboBox<>();
    private final JComboBox<String> status = new JComboBox<>(new String[]{"AGENDADA", "EM_ATENDIMENTO", "CONCLUIDA", "CANCELADA"});
    private final JTextField dataHora = Ui.campo();
    private final JTextField motivo = Ui.campo();
    private final JTextField diagnostico = Ui.campo();
    private final JTextField quantidade = Ui.campo();
    private final JLabel total = new JLabel("Total: 0,00");
    private int idAtual;

    public ConsultaPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        JButton pesquisar = Ui.botao("Pesquisar");
        pesquisar.addActionListener(e -> carregar());
        add(Ui.barra(busca, pesquisar), BorderLayout.NORTH);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionada();
            }
        });
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Ui.tabela(tabela), montarFormulario());
        split.setResizeWeight(0.45);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        recarregarCombos();
        carregar();
    }

    private JPanel montarFormulario() {
        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(8, 0, 0, 0));
        JPanel topo = new JPanel(new GridLayout(2, 3, 8, 8));
        topo.setOpaque(false);
        topo.add(Ui.rotulo("Animal", animal));
        topo.add(Ui.rotulo("Veterinário", veterinario));
        topo.add(Ui.rotulo("Data e hora (aaaa-mm-dd HH:mm)", dataHora));
        topo.add(Ui.rotulo("Motivo", motivo));
        topo.add(Ui.rotulo("Diagnóstico", diagnostico));
        topo.add(Ui.rotulo("Status", status));
        form.add(topo, BorderLayout.NORTH);

        JButton addItem = Ui.botao("Adicionar item");
        JButton remItem = Ui.botao("Remover item");
        addItem.addActionListener(e -> adicionarItem());
        remItem.addActionListener(e -> {
            int row = itensTabela.getSelectedRow();
            if (row >= 0) {
                modeloItens.removeRow(row);
                atualizarTotal();
            }
        });
        JPanel itensBarra = Ui.barra(procedimento, quantidade, addItem, remItem, total);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(itensBarra, BorderLayout.NORTH);
        centro.add(new JScrollPane(itensTabela), BorderLayout.CENTER);
        form.add(centro, BorderLayout.CENTER);

        JButton novo = Ui.botao("Nova");
        JButton salvar = Ui.botao("Salvar consulta");
        JButton cancelar = Ui.botao("Cancelar consulta");
        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        cancelar.addActionListener(e -> cancelar());
        form.add(Ui.barra(novo, salvar, cancelar), BorderLayout.SOUTH);
        quantidade.setText("1");
        animal.setRenderer((list, value, i, sel, foc) -> rotulo(list, value == null ? "" : value.getNome() + " (#" + value.getId() + ")", sel));
        veterinario.setRenderer((list, value, i, sel, foc) -> rotulo(list, value == null ? "" : value.getNome(), sel));
        procedimento.setRenderer((list, value, i, sel, foc) -> rotulo(list, value == null ? "" : value.getNome() + " — " + value.getValor(), sel));
        return form;
    }

    private javax.swing.JLabel rotulo(javax.swing.JList<?> list, String texto, boolean sel) {
        javax.swing.JLabel l = new javax.swing.JLabel(texto);
        if (sel) {
            l.setOpaque(true);
            l.setBackground(list.getSelectionBackground());
        }
        return l;
    }

    public void recarregarCombos() {
        animal.removeAllItems();
        for (Animal a : new AnimalController().listar("")) {
            animal.addItem(a);
        }
        veterinario.removeAllItems();
        for (Veterinario v : new VeterinarioController().listar("")) {
            veterinario.addItem(v);
        }
        procedimento.removeAllItems();
        for (Procedimento p : new ProcedimentoController().listar("")) {
            procedimento.addItem(p);
        }
    }

    private void carregar() {
        modelo.setRowCount(0);
        for (ConsultaDao.Linha linha : controller.listar(busca.getText())) {
            Consulta c = linha.consulta;
            modelo.addRow(new Object[]{c.getId(), c.getDataHora().format(FMT), linha.animal, linha.tutor,
                    linha.veterinario, c.getStatus(), c.getValorTotal()});
        }
    }

    private void carregarSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return;
        }
        idAtual = (int) modelo.getValueAt(row, 0);
        dataHora.setText(String.valueOf(modelo.getValueAt(row, 1)));
        motivo.setText("");
        status.setSelectedItem(String.valueOf(modelo.getValueAt(row, 5)));
        selecionarAnimal(String.valueOf(modelo.getValueAt(row, 2)));
        selecionarVet(String.valueOf(modelo.getValueAt(row, 4)));
        modeloItens.setRowCount(0);
        for (ItemConsulta item : controller.itens(idAtual)) {
            Procedimento p = acharProcedimento(item.getProcedimentoId());
            modeloItens.addRow(new Object[]{item.getProcedimentoId(), p == null ? item.getProcedimentoId() : p.getNome(),
                    item.getQuantidade(), item.getValorUnitario()});
        }
        atualizarTotal();
    }

    private void selecionarAnimal(String nome) {
        for (int i = 0; i < animal.getItemCount(); i++) {
            if (animal.getItemAt(i).getNome().equals(nome)) {
                animal.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarVet(String nome) {
        for (int i = 0; i < veterinario.getItemCount(); i++) {
            if (veterinario.getItemAt(i).getNome().equals(nome)) {
                veterinario.setSelectedIndex(i);
                return;
            }
        }
    }

    private Procedimento acharProcedimento(int id) {
        for (int i = 0; i < procedimento.getItemCount(); i++) {
            if (procedimento.getItemAt(i).getId() == id) {
                return procedimento.getItemAt(i);
            }
        }
        return null;
    }

    private void adicionarItem() {
        Procedimento p = (Procedimento) procedimento.getSelectedItem();
        if (p == null) {
            Ui.erro(this, "Cadastre um procedimento antes.");
            return;
        }
        int qtd;
        try {
            qtd = Integer.parseInt(quantidade.getText().trim());
        } catch (NumberFormatException ex) {
            Ui.erro(this, "Quantidade inválida.");
            return;
        }
        if (qtd <= 0) {
            Ui.erro(this, "Quantidade deve ser maior que zero.");
            return;
        }
        modeloItens.addRow(new Object[]{p.getId(), p.getNome(), qtd, p.getValor()});
        atualizarTotal();
    }

    private void atualizarTotal() {
        BigDecimal soma = BigDecimal.ZERO;
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            int qtd = Integer.parseInt(String.valueOf(modeloItens.getValueAt(i, 2)));
            BigDecimal valor = new BigDecimal(String.valueOf(modeloItens.getValueAt(i, 3)));
            soma = soma.add(valor.multiply(BigDecimal.valueOf(qtd)));
        }
        total.setText("Total: " + soma);
    }

    private void salvar() {
        try {
            Animal a = (Animal) animal.getSelectedItem();
            Veterinario v = (Veterinario) veterinario.getSelectedItem();
            Consulta c = new Consulta();
            c.setId(idAtual);
            c.setAnimalId(a == null ? 0 : a.getId());
            c.setVeterinarioId(v == null ? 0 : v.getId());
            c.setDataHora(LocalDateTime.parse(dataHora.getText().trim(), FMT));
            c.setMotivo(motivo.getText().trim());
            c.setDiagnostico(diagnostico.getText().trim());
            c.setStatus(String.valueOf(status.getSelectedItem()));
            List<ItemConsulta> itens = new ArrayList<>();
            for (int i = 0; i < modeloItens.getRowCount(); i++) {
                ItemConsulta item = new ItemConsulta();
                item.setProcedimentoId(Integer.parseInt(String.valueOf(modeloItens.getValueAt(i, 0))));
                item.setQuantidade(Integer.parseInt(String.valueOf(modeloItens.getValueAt(i, 2))));
                item.setValorUnitario(new BigDecimal(String.valueOf(modeloItens.getValueAt(i, 3))));
                itens.add(item);
            }
            controller.salvar(c, itens);
            Ui.info(this, "Consulta salva.");
            limpar();
            carregar();
        } catch (DateTimeParseException ex) {
            Ui.erro(this, "Data inválida. Use aaaa-mm-dd HH:mm.");
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void cancelar() {
        if (idAtual == 0) {
            Ui.erro(this, "Selecione uma consulta.");
            return;
        }
        if (Ui.confirmar(this, "Cancelar esta consulta?")) {
            controller.cancelar(idAtual);
            carregar();
        }
    }

    private void limpar() {
        idAtual = 0;
        dataHora.setText(LocalDateTime.now().format(FMT));
        motivo.setText("");
        diagnostico.setText("");
        status.setSelectedIndex(0);
        modeloItens.setRowCount(0);
        quantidade.setText("1");
        atualizarTotal();
        tabela.clearSelection();
    }
}
