package br.edu.vetcare.model;

import java.math.BigDecimal;

public class ItemConsulta {
    private int id;
    private int consultaId;
    private int procedimentoId;
    private int quantidade = 1;
    private BigDecimal valorUnitario;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getConsultaId() { return consultaId; }
    public void setConsultaId(int consultaId) { this.consultaId = consultaId; }
    public int getProcedimentoId() { return procedimentoId; }
    public void setProcedimentoId(int procedimentoId) { this.procedimentoId = procedimentoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}
