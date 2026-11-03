-- ============================================================
--  FineArts - Script SQL (MySQL 8.0+)  |  Etapa 6 - núcleo reutilizável
--  Cria o banco, as tabelas e os dados iniciais de teste.
--  Senhas armazenadas como hash PBKDF2 (nunca em texto puro).
--  Contas de teste:  admin@finearts.com / admin123
--                    ana@finearts.com, carlos@finearts.com,
--                    mariana@finearts.com, joao@email.com / 123456
-- ============================================================
DROP DATABASE IF EXISTS finearts;
CREATE DATABASE finearts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE finearts;

CREATE TABLE usuarios (
    id            INT          NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(150) NOT NULL,
    cpf           CHAR(14)     NOT NULL,
    email         VARCHAR(200) NOT NULL,
    senha         VARCHAR(255) NOT NULL,
    tipo          ENUM('ADMINISTRADOR','ARTISTA','CLIENTE') NOT NULL,
    data_cadastro DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuarios_cpf (cpf),
    UNIQUE KEY uq_usuarios_email (email)
) ENGINE=InnoDB;

CREATE TABLE artistas (
    usuario_id    INT          NOT NULL,
    telefone      VARCHAR(20),
    formacao      VARCHAR(200),
    redes_sociais VARCHAR(300),
    aprovado      TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (usuario_id),
    CONSTRAINT fk_artistas_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE clientes (
    usuario_id   INT         NOT NULL,
    telefone     VARCHAR(20),
    tipo_cliente ENUM('apreciador','colecionador','curador'),
    PRIMARY KEY (usuario_id),
    CONSTRAINT fk_clientes_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE obras_de_arte (
    id         INT           NOT NULL AUTO_INCREMENT,
    artista_id INT           NOT NULL,
    titulo     VARCHAR(200)  NOT NULL,
    tecnica    VARCHAR(100)  NOT NULL,
    estilo     VARCHAR(100),
    `local`    VARCHAR(150),
    ano        SMALLINT      NOT NULL,
    largura_cm DECIMAL(7,2)  NOT NULL,
    altura_cm  DECIMAL(7,2)  NOT NULL,
    preco      DECIMAL(12,2) NOT NULL,
    disponivel TINYINT(1)    NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    INDEX idx_obras_disponivel (disponivel),
    CONSTRAINT fk_obras_artista FOREIGN KEY (artista_id)
        REFERENCES artistas(usuario_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pedidos (
    id              INT           NOT NULL AUTO_INCREMENT,
    cliente_id      INT           NOT NULL,
    valor_total     DECIMAL(12,2) NOT NULL,
    forma_pagamento ENUM('PIX','CARTAO_CREDITO','CARTAO_DEBITO','DINHEIRO') NOT NULL,
    status          ENUM('AGUARDANDO_PAGAMENTO','PAGO','PREPARANDO_ENVIO','ENVIADO','ENTREGUE','CANCELADO')
                    NOT NULL DEFAULT 'PAGO',
    data_hora       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_pedidos_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes(usuario_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE itens_pedido (
    pedido_id      INT           NOT NULL,
    obra_id        INT           NOT NULL,
    preco_unitario DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (pedido_id, obra_id),
    CONSTRAINT fk_itens_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_itens_obra FOREIGN KEY (obra_id)
        REFERENCES obras_de_arte(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE certificados (
    id           INT         NOT NULL AUTO_INCREMENT,
    codigo       VARCHAR(50) NOT NULL,
    obra_id      INT         NOT NULL,
    pedido_id    INT         NOT NULL,
    comprador_id INT         NOT NULL,
    data_emissao DATE        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_certificados_codigo (codigo),
    CONSTRAINT fk_cert_obra FOREIGN KEY (obra_id)
        REFERENCES obras_de_arte(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cert_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cert_comprador FOREIGN KEY (comprador_id)
        REFERENCES clientes(usuario_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ===================== DADOS INICIAIS =====================
INSERT INTO usuarios (nome, cpf, email, senha, tipo, data_cadastro) VALUES
('Administrador',  '390.533.447-05', 'admin@finearts.com',    'PBKDF2$120000$jgRPKdceMu00CfXWeSuuRg==$ZAI8Ge3KIKZ2dA3dN1NB1pfUaook6rS0GNEHVOhSXKI=', 'ADMINISTRADOR', CURDATE()),
('Ana Lima',       '123.456.789-09', 'ana@finearts.com',      'PBKDF2$120000$aRm+GecX+nT0wd1pbLyNyw==$ClDMmemcTkxcsSjHRcOG9k/7Xovx55e2OqSRuLbjIO8=', 'ARTISTA',       CURDATE()),
('Carlos Melo',    '987.654.321-00', 'carlos@finearts.com',   'PBKDF2$120000$aRm+GecX+nT0wd1pbLyNyw==$ClDMmemcTkxcsSjHRcOG9k/7Xovx55e2OqSRuLbjIO8=', 'ARTISTA',       CURDATE()),
('Mariana Costa',  '246.813.579-28', 'mariana@finearts.com',  'PBKDF2$120000$aRm+GecX+nT0wd1pbLyNyw==$ClDMmemcTkxcsSjHRcOG9k/7Xovx55e2OqSRuLbjIO8=', 'CLIENTE',       CURDATE()),
('João Pereira',   '135.792.468-28', 'joao@email.com',        'PBKDF2$120000$aRm+GecX+nT0wd1pbLyNyw==$ClDMmemcTkxcsSjHRcOG9k/7Xovx55e2OqSRuLbjIO8=', 'CLIENTE',       CURDATE());

INSERT INTO artistas (usuario_id, telefone, formacao, aprovado) VALUES
(2, '(65) 99999-0001', 'Belas Artes - USP', 1),
(3, '(21) 99999-0002', 'Escola de Artes Visuais - RJ', 1);

INSERT INTO clientes (usuario_id, telefone, tipo_cliente) VALUES
(4, '(11) 98888-5555', 'colecionador'),
(5, '(21) 97777-4444', 'apreciador');

INSERT INTO obras_de_arte (artista_id, titulo, tecnica, estilo, `local`, ano, largura_cm, altura_cm, preco) VALUES
(2, 'Sertão Dourado',     'Óleo sobre tela', 'Realismo',       'Cuiabá',          2023,  80.00,  60.00,  3500.00),
(2, 'Flores do Cerrado',  'Aquarela',        'Impressionismo', 'Rondonópolis',    2022,  40.00,  30.00,  1200.00),
(2, 'Manhã no Araguaia',  'Pastel seco',     'Paisagismo',     'Barra do Garças', 2022,  60.00,  45.00,  2200.00),
(3, 'Abstrações Urbanas', 'Acrílica',        'Abstrato',       'São Paulo',       2024, 100.00,  80.00,  7800.00),
(3, 'Pantanal Vivo',      'Óleo sobre tela', 'Realismo',       'Corumbá',         2023, 120.00,  90.00, 12000.00),
(3, 'Geometria Tropical', 'Acrílica',        'Abstrato',       'Rio de Janeiro',  2024,  90.00,  70.00,  5500.00);

-- Pedido de demonstração da Mariana (obras 1 e 2 já vendidas)
INSERT INTO pedidos (cliente_id, valor_total, forma_pagamento, status, data_hora)
VALUES (4, 4700.00, 'PIX', 'ENTREGUE', '2026-04-10 14:32:00');
INSERT INTO itens_pedido (pedido_id, obra_id, preco_unitario) VALUES (1, 1, 3500.00), (1, 2, 1200.00);
UPDATE obras_de_arte SET disponivel = 0 WHERE id IN (1, 2);
INSERT INTO certificados (codigo, obra_id, pedido_id, comprador_id, data_emissao) VALUES
('CERT-0001-2026', 1, 1, 4, '2026-04-10'),
('CERT-0002-2026', 2, 1, 4, '2026-04-10');
