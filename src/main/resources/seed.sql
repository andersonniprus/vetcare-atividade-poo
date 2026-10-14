-- Dados iniciais. Inseridos só se o banco estiver vazio.
INSERT INTO usuario (login, senha, nome, perfil) VALUES
 ('admin', 'admin', 'Administrador', 'ADMIN'),
 ('recepcao', '1234', 'Recepção', 'RECEPCAO');

INSERT INTO cliente (nome, cpf, telefone, email, endereco) VALUES
 ('Ana Souza', '123.456.789-00', '(47) 99911-2200', 'ana@email.com', 'Rua das Flores, 120'),
 ('Carlos Lima', '987.654.321-00', '(47) 98820-1100', 'carlos@email.com', 'Av. Brasil, 45');

INSERT INTO animal (cliente_id, nome, especie, raca, sexo, data_nascimento, peso_kg) VALUES
 (1, 'Thor', 'Cão', 'Labrador', 'M', '2020-03-12', 28.40),
 (1, 'Luna', 'Gato', 'SRD', 'F', '2022-07-01', 4.20),
 (2, 'Bidu', 'Cão', 'Vira-lata', 'M', '2019-11-20', 12.00);

INSERT INTO veterinario (nome, crmv, especialidade, telefone, email) VALUES
 ('Marina Alves', 'SC-12345', 'Clínica geral', '(47) 98800-1001', 'marina@vetcare.local'),
 ('Paulo Neri', 'SC-22910', 'Cirurgia', '(47) 98800-1002', 'paulo@vetcare.local');

INSERT INTO procedimento (nome, descricao, valor) VALUES
 ('Consulta clínica', 'Avaliação geral', 120.00),
 ('Vacina V10', 'Imunização polivalente', 90.00),
 ('Hemograma', 'Exame laboratorial', 75.00),
 ('Castração', 'Procedimento cirúrgico', 450.00);
