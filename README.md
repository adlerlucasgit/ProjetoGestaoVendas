# 📦 Sistema de Gestão de Vendas e Controle de Estoque – EletroTech

## 📖 Sobre o Projeto

Este projeto consiste no desenvolvimento de um sistema comercial, com o objetivo de automatizar o controle de estoque e o gerenciamento de vendas.

A solução foi criada para substituir processos manuais (planilhas e cadernos), trazendo mais **eficiência, segurança e confiabilidade**.

---

## ⚙️ Funcionalidades

### 👤 1. Módulo de Usuários

* Cadastro de funcionários (vendedores, gerentes, estoquistas)
* Sistema de autenticação (login obrigatório)

---

### 👥 2. Módulo de Clientes

* Cadastro com validação de **CPF/CNPJ**
* Bloqueio de cadastros duplicados
* Busca rápida por nome, CPF ou e-mail
* Controle de status:

  * Ativo
  * Inativo (não pode comprar)
* Exibição das **últimas 5 compras**

---

### 📦 3. Módulo de Produtos

* Cadastro de:

  * Código
  * Descrição
  * Preço de custo
  * Preço de venda
* Estoque inicial = **0**
* Código de barras único
* Organização por categorias
* Cálculo automático de preço de venda (com margem %)
* Alerta de estoque mínimo

---

### 🔄 4. Controle de Estoque

* Registro de entradas e saídas
* Histórico completo de movimentações:

  * Usuário responsável
  * Data e hora
  * Quantidade alterada

---

### 💰 5. Módulo de Vendas (PDV)

* Seleção de cliente
* Adição de produtos ao carrinho
* Controle de múltiplas formas de pagamento:

  * Dinheiro
  * Cartão
* Cálculo automático de troco
* Aplicação de desconto:

  * Até 5% → permitido ao vendedor
  * Acima de 5% → exige senha de gerente
* Geração de comprovante (cupom não fiscal)

---

## 📌 Regras de Negócio

* **RN01 – Baixa Automática de Estoque**
  Ao finalizar a venda, o estoque é atualizado automaticamente.

* **RN02 – Bloqueio de Venda Sem Estoque**
  Não permite vender produtos com estoque insuficiente.

* **RN03 – Estorno de Estoque**
  Cancelamentos retornam os produtos ao estoque com registro do motivo.

---

## 🛠️ Tecnologias Utilizadas

* Java
* JavaFX (Interface gráfica)
* MySQL (Banco de dados)
* JDBC (Conexão com banco)

---

## 🚀 Como Executar o Projeto

### 🔧 Pré-requisitos

* Java JDK 8+
* MySQL instalado
* IDE (Eclipse ou IntelliJ)

### 📥 Passos

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
   ```

2. Importe o projeto na sua IDE

3. Configure o banco de dados:

   * Crie o banco no MySQL
   * Execute o script SQL disponível no projeto

4. Configure a conexão no arquivo:

   ```
   Conexao.java
   ```

5. Execute a aplicação

---

## 📊 Estrutura do Sistema (Resumo)

```
📁 application
 ├── model        → Classes de dados
 ├── view         → Interfaces (JavaFX)
 ├── dao          → Acesso ao banco de dados
 ├── util         → Conexão com o banco de dados
```

---

## 🧪 Fluxo Principal do Sistema

1. Login do usuário
2. Cadastro/consulta de cliente
3. Cadastro/consulta de produtos
4. Entrada de estoque
5. Realização de venda
6. Atualização automática do estoque
7. Emissão do comprovante

---

## 📌 Observações Finais

Este sistema foi projetado com foco em **boas práticas de desenvolvimento**, separação de camadas (MVC) e regras de negócio bem definidas, garantindo escalabilidade e manutenção futura.

---
