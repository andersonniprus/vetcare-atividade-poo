package br.edu.vetcare.controller;

import br.edu.vetcare.dao.VeterinarioDao;
import br.edu.vetcare.model.Veterinario;

import java.util.List;

public class VeterinarioController {
    private final VeterinarioDao dao = new VeterinarioDao();

    public List<Veterinario> listar(String filtro) {
        return dao.listar(filtro);
    }

    public void salvar(Veterinario v) {
        if (v.getNome() == null || v.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do veterinário.");
        }
        if (v.getCrmv() == null || v.getCrmv().isBlank()) {
            throw new IllegalArgumentException("Informe o CRMV.");
        }
        if (v.getEspecialidade() == null || v.getEspecialidade().isBlank()) {
            throw new IllegalArgumentException("Informe a especialidade.");
        }
        dao.salvar(v);
    }

    public void inativar(int id) {
        dao.inativar(id);
    }
}
