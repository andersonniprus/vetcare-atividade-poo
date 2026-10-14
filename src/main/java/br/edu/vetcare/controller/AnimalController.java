package br.edu.vetcare.controller;

import br.edu.vetcare.dao.AnimalDao;
import br.edu.vetcare.model.Animal;

import java.util.List;

public class AnimalController {
    private final AnimalDao dao = new AnimalDao();

    public List<Animal> listar(String filtro) {
        return dao.listar(filtro);
    }

    public void salvar(Animal animal) {
        if (animal.getNome() == null || animal.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do animal.");
        }
        if (animal.getEspecie() == null || animal.getEspecie().isBlank()) {
            throw new IllegalArgumentException("Informe a espécie.");
        }
        if (animal.getClienteId() <= 0) {
            throw new IllegalArgumentException("Selecione o tutor.");
        }
        if (animal.getSexo() != 'M' && animal.getSexo() != 'F') {
            throw new IllegalArgumentException("Sexo deve ser M ou F.");
        }
        dao.salvar(animal);
    }

    public void inativar(int id) {
        dao.inativar(id);
    }
}
