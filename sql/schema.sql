-- VetCare - Clínica Veterinária
-- Banco: MySQL 8+ / MariaDB
-- Charset: utf8mb4

CREATE DATABASE IF NOT EXISTS vetcare
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE vetcare;

CREATE TABLE cliente (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    cpf           VARCHAR(14)  NOT NULL UNIQUE,
    telefone      VARCHAR(20)  NOT NULL,
    email         VARCHAR(120),
    endereco      VARCHAR(200),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE animal (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id       INT          NOT NULL,
    nome             VARCHAR(80)  NOT NULL,
    especie          VARCHAR(40)  NOT NULL,
    raca             VARCHAR(60),
    sexo             CHAR(1)      NOT NULL,
    data_nascimento  DATE,
    peso_kg          DECIMAL(5,2),
    observacoes      VARCHAR(300),
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_animal_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT ck_animal_sexo CHECK (sexo IN ('M', 'F'))
);

CREATE TABLE veterinario (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    crmv           VARCHAR(20)  NOT NULL UNIQUE,
    especialidade  VARCHAR(80)  NOT NULL,
    telefone       VARCHAR(20),
    email          VARCHAR(120),
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE procedimento (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100)   NOT NULL,
    descricao   VARCHAR(300),
    valor       DECIMAL(10,2)  NOT NULL,
    ativo       BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_procedimento_valor CHECK (valor >= 0)
);

CREATE TABLE consulta (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    animal_id        INT          NOT NULL,
    veterinario_id   INT          NOT NULL,
    data_hora        DATETIME     NOT NULL,
    motivo           VARCHAR(200) NOT NULL,
    diagnostico      VARCHAR(400),
    status           VARCHAR(20)  NOT NULL DEFAULT 'AGENDADA',
    valor_total      DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_consulta_animal
        FOREIGN KEY (animal_id) REFERENCES animal(id),
    CONSTRAINT fk_consulta_veterinario
        FOREIGN KEY (veterinario_id) REFERENCES veterinario(id),
    CONSTRAINT ck_consulta_status
        CHECK (status IN ('AGENDADA', 'EM_ATENDIMENTO', 'CONCLUIDA', 'CANCELADA'))
);

CREATE TABLE item_consulta (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id       INT           NOT NULL,
    procedimento_id   INT           NOT NULL,
    quantidade        INT           NOT NULL DEFAULT 1,
    valor_unitario    DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_consulta
        FOREIGN KEY (consulta_id) REFERENCES consulta(id),
    CONSTRAINT fk_item_procedimento
        FOREIGN KEY (procedimento_id) REFERENCES procedimento(id),
    CONSTRAINT ck_item_qtd CHECK (quantidade > 0)
);

CREATE INDEX idx_animal_cliente ON animal(cliente_id);
CREATE INDEX idx_consulta_animal ON consulta(animal_id);
CREATE INDEX idx_consulta_vet ON consulta(veterinario_id);
CREATE INDEX idx_consulta_data ON consulta(data_hora);
CREATE INDEX idx_item_consulta ON item_consulta(consulta_id);
