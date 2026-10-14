-- VetCare schema (H2 em modo MySQL e MySQL 8)
CREATE TABLE IF NOT EXISTS usuario (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    login     VARCHAR(40)  NOT NULL UNIQUE,
    senha     VARCHAR(40)  NOT NULL,
    nome      VARCHAR(80)  NOT NULL,
    perfil    VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS cliente (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(120) NOT NULL,
    cpf        VARCHAR(14)  NOT NULL UNIQUE,
    telefone   VARCHAR(20)  NOT NULL,
    email      VARCHAR(120),
    endereco   VARCHAR(200),
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS animal (
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
    CONSTRAINT fk_animal_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS veterinario (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    crmv           VARCHAR(20)  NOT NULL UNIQUE,
    especialidade  VARCHAR(80)  NOT NULL,
    telefone       VARCHAR(20),
    email          VARCHAR(120),
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS procedimento (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100)   NOT NULL,
    descricao  VARCHAR(300),
    valor      DECIMAL(10,2)  NOT NULL,
    ativo      BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS consulta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    animal_id       INT           NOT NULL,
    veterinario_id  INT           NOT NULL,
    data_hora       TIMESTAMP     NOT NULL,
    motivo          VARCHAR(200)  NOT NULL,
    diagnostico     VARCHAR(400),
    status          VARCHAR(20)   NOT NULL DEFAULT 'AGENDADA',
    valor_total     DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_consulta_animal FOREIGN KEY (animal_id) REFERENCES animal(id),
    CONSTRAINT fk_consulta_vet FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

CREATE TABLE IF NOT EXISTS item_consulta (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id      INT           NOT NULL,
    procedimento_id  INT           NOT NULL,
    quantidade       INT           NOT NULL DEFAULT 1,
    valor_unitario   DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_consulta FOREIGN KEY (consulta_id) REFERENCES consulta(id),
    CONSTRAINT fk_item_procedimento FOREIGN KEY (procedimento_id) REFERENCES procedimento(id)
);
