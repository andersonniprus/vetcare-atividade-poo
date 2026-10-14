package br.edu.vetcare.controller;

import br.edu.vetcare.dao.ConsultaDao;
import br.edu.vetcare.model.Consulta;
import br.edu.vetcare.model.ItemConsulta;

import java.util.List;

public class ConsultaController {
    private final ConsultaDao dao = new ConsultaDao();

    public List<ConsultaDao.Linha> listar(String filtro) {
        return dao.listar(filtro);
    }

    public List<ItemConsulta> itens(int consultaId) {
        return dao.itens(consultaId);
    }

    public void salvar(Consulta consulta, List<ItemConsulta> itens) {
        if (consulta.getAnimalId() <= 0) {
            throw new IllegalArgumentException("Selecione o animal.");
        }
        if (consulta.getVeterinarioId() <= 0) {
            throw new IllegalArgumentException("Selecione o veterinário.");
        }
        if (consulta.getDataHora() == null) {
            throw new IllegalArgumentException("Informe a data e a hora.");
        }
        if (consulta.getMotivo() == null || consulta.getMotivo().isBlank()) {
            throw new IllegalArgumentException("Informe o motivo.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Inclua ao menos um procedimento.");
        }
        dao.salvar(consulta, itens);
    }

    public void cancelar(int id) {
        dao.cancelar(id);
    }
}
