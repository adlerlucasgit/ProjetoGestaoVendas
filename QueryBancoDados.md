create database GestaoVendasDB;
use GestaoVendasDB;

create table usuarios(
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100),
login VARCHAR(50) UNIQUE,
senha VARCHAR(255),
tipo ENUM('VENDEDOR','GERENTE','ESTOQUISTA'));

insert into usuarios(nome, login, senha, tipo)
values('admin','admin','1234','GERENTE');

create table clientes(
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100),
cpf_cnpj VARCHAR(20) UNIQUE,
email VARCHAR(100) UNIQUE,
status ENUM('ATIVO','INATIVO'));

CREATE TABLE estornos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT,
    motivo VARCHAR(255),
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table produtos(
id INT AUTO_INCREMENT PRIMARY KEY,
nome varchar(100),
codigo_barras VARCHAR(50) UNIQUE,
descricao VARCHAR(100),
preco_custo DOUBLE,
preco_venda DOUBLE,
estoque INT DEFAULT 0,
estoque_minimo INT,
categoria VARCHAR(50));

create table estoqueMovimentacao(
id INT AUTO_INCREMENT PRIMARY KEY,
produto_id INT,
tipo ENUM('ENTRADA','SAIDA'),
quantidade INT,
data DATETIME,
FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    total DOUBLE NOT NULL,
    data DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('FINALIZADA','CANCELADA') DEFAULT 'FINALIZADA',
    motivo_cancelamento TEXT,

    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE vendaItens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco DOUBLE NOT NULL,

    FOREIGN KEY (venda_id) REFERENCES vendas(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE pagamentos (
    id INT PRIMARY KEY auto_increment,
    venda_id INT,
    tipo VARCHAR(50),
    valor DECIMAL(10,2),

    FOREIGN KEY (venda_id) REFERENCES vendas(id)
);
