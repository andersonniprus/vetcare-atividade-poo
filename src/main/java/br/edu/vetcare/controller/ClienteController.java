package br.edu.vetcare.controller;

import br.edu.vetcare.dao.ClienteDao;
import br.edu.vetcare.model.Cliente;

import java.util.List;

public class ClienteController {
    private final ClienteDao dao = new ClienteDao();

    public List<Cliente> listar(String filtro) {
        return dao.listar(filtro);
    }

    public void salvar(Cliente cliente) {
        validar(cliente);
        dao.salvar(cliente);
    }

    public void inativar(int id) {
        dao.inativar(id);
    }

    private void validar(Cliente c) {
        if (c.getNome() == null || c.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do cliente.");
        }
        if (c.getCpf() == null || c.getCpf().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF.");
        }
        if (c.getTelefone() == null || c.getTelefone().isBlank()) {
            throw new IllegalArgumentException("Informe o telefone.");
        }
    }
}
