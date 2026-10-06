package br.edu.vetcare.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consulta {
    private int id;
    private int animalId;
    private int veterinarioId;
    private LocalDateTime dataHora;
    private String motivo;
    private String diagnostico;
    private String status = "AGENDADA";
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }
    public int getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(int veterinarioId) { this.veterinarioId = veterinarioId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
