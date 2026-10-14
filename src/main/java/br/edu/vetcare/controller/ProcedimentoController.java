package br.edu.vetcare.controller;

import br.edu.vetcare.dao.ProcedimentoDao;
import br.edu.vetcare.model.Procedimento;

import java.math.BigDecimal;
import java.util.List;

public class ProcedimentoController {
    private final ProcedimentoDao dao = new ProcedimentoDao();

    public List<Procedimento> listar(String filtro) {
        return dao.listar(filtro);
    }

    public void salvar(Procedimento p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do procedimento.");
        }
        if (p.getValor() == null || p.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Informe um valor válido.");
        }
        dao.salvar(p);
    }

    public void inativar(int id) {
        dao.inativar(id);
    }
}
