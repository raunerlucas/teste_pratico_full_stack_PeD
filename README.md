# 🏭 Gestão Industrial — Gerenciamento de Insumos e Otimização de Produção

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4.2-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-1021FF?style=for-the-badge&logo=databricks&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

<div align="center">

**Aplicação Full Stack para gerenciamento de matérias-primas, produtos e otimização de produção industrial.**

Uma fábrica precisa controlar seu estoque de insumos e decidir **o que produzir** para obter o **maior lucro possível** com o material disponível.

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Como Rodar](#-como-rodar)
  - [Docker (recomendado)](#-docker-recomendado)
  - [Execução Manual](#-execução-manual)
- [Endpoints da API](#-endpoints-da-api)
- [Testes](#-testes)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Screenshots](#-screenshots)

---

## 🎯 Sobre o Projeto

Sistema completo que permite a um operador de fábrica:

1. **Gerenciar Matérias-Primas** — cadastro, edição, listagem e exclusão de insumos com controle de estoque.
2. **Gerenciar Produtos** — cadastro de produtos com composição dinâmica (quais matérias-primas e quantidades são necessárias para fabricar 1 unidade).
3. **Otimizar a Produção** — algoritmo que analisa o estoque atual e sugere quais produtos fabricar para maximizar o **valor total de venda**, resolvendo conflitos quando dois produtos disputam a mesma matéria-prima.

---

## ✨ Funcionalidades

### Requisitos Obrigatórios ✅

| Requisito | Status |
|---|:---:|
| CRUD completo de Matérias-Primas (código, nome, estoque) | ✅ |
| CRUD completo de Produtos (código, nome, valor, composição) | ✅ |
| API de Cálculo de Produção otimizada | ✅ |
| Tela de otimização com estoque + resultado | ✅ |
| Prioriza produtos de maior retorno financeiro | ✅ |
| Resolve conflitos de matéria-prima compartilhada | ✅ |
| Código limpo (Clean Code) em inglês | ✅ |
| Testes unitários no back-end (lógica de produção) | ✅ |

### Diferenciais ✅

| Diferencial | Status |
|---|:---:|
| Testes unitários no front-end | ✅ 25 testes |
| Internacionalização (pt-BR / en) | ✅ vue-i18n |
| Dockerização completa (front + back) | ✅ |
| Swagger / OpenAPI documentada | ✅ |
| Dados iniciais via DataLoader | ✅ |

---

## 🛠 Tecnologias

### Back-end

| Tecnologia | Versão | Finalidade |
|---|---|---|
| **Java** | 21 | Linguagem |
| **Spring Boot** | 4.0.3 | Framework REST |
| **Spring Data JPA** | — | Persistência ORM |
| **Spring Security** | — | Configuração CORS |
| **H2 Database** | — | Banco relacional em memória |
| **Lombok** | — | Redução de boilerplate |
| **SpringDoc OpenAPI** | 2.8.6 | Documentação Swagger |
| **JUnit 6 + Mockito** | — | Testes unitários |

### Front-end

| Tecnologia | Versão | Finalidade |
|---|---|---|
| **Vue.js 3** | 3.5.29 | Framework reativo (Composition API + `<script setup>`) |
| **Vite** | 7.3.1 | Build tool e dev server |
| **Vue Router** | 4.x | Roteamento SPA |
| **Pinia** | 3.x | Gerenciamento de estado global |
| **Axios** | — | Requisições HTTP |
| **Tailwind CSS** | 4.2 | Estilização utilitária |
| **Vue I18n** | 9.x | Internacionalização (pt-BR / en) |
| **Vitest** | 4.x | Testes unitários |

### Infraestrutura

| Tecnologia | Finalidade |
|---|---|
| **Docker** | Containerização |
| **Docker Compose** | Orquestração front + back |
| **Nginx** | Servir SPA + reverse proxy para API |

---

## 🏗 Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                      Docker Compose                         │
│                                                             │
│   ┌─────────────────────┐     ┌─────────────────────────┐  │
│   │   Frontend (Nginx)  │     │   Backend (Spring Boot)  │  │
│   │   ┌───────────────┐ │     │   ┌──────────────────┐   │  │
│   │   │  Vue.js 3 SPA │ │     │   │  REST Controllers │   │  │
│   │   │  + Tailwind   │ │     │   │  Services         │   │  │
│   │   │  + Pinia      │ │────▶│   │  Repositories     │   │  │
│   │   │  + Vue Router │ │     │   │  DTOs / Entities  │   │  │
│   │   └───────────────┘ │     │   └──────────────────┘   │  │
│   │       Porta 80      │     │   ┌──────────────────┐   │  │
│   └─────────────────────┘     │   │   H2 Database    │   │  │
│                               │   │   (in-memory)    │   │  │
│                               │   └──────────────────┘   │  │
│                               │       Porta 8080         │  │
│                               └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Algoritmo de Otimização — Greedy (Guloso)

1. Carrega todos os produtos e o estoque atual de matérias-primas.
2. Ordena os produtos por **preço unitário decrescente** (maior valor primeiro).
3. Para cada produto, calcula o **máximo de unidades fabricáveis** (gargalo = matéria-prima mais escassa).
4. Consome o estoque virtual e registra a sugestão.
5. Repete até esgotar a lista.

> **Complexidade:** `O(P × C)`, onde P = produtos e C = composições por produto.

---

## 🚀 Como Rodar

### 🐳 Docker (recomendado)

> **Pré-requisitos:** [Docker](https://docs.docker.com/get-docker/) e [Docker Compose](https://docs.docker.com/compose/install/) instalados.

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/teste_pratico_full_stack_PeD.git
cd teste_pratico_full_stack_PeD

# 2. Suba toda a aplicação com um único comando
docker-compose up --build
```

| Serviço | URL |
|---|---|
| **Frontend** | [http://localhost](http://localhost) |
| **API REST** | [http://localhost/api](http://localhost/api/raw-materials) |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **H2 Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |

> Para encerrar: `docker-compose down`

---

### 💻 Execução Manual

#### Pré-requisitos

- **Java** 21+
- **Maven** 3.9+ (ou use o wrapper `./mvnw`)
- **Node.js** 20.19+ ou 22.12+
- **npm** 10+

#### Back-end

```bash
cd backend

# Instalar dependências e rodar
./mvnw spring-boot:run

# A API estará em http://localhost:8080
```

#### Front-end

```bash
cd frontend

# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm run dev

# A aplicação estará em http://localhost:5173
```

---

## 📡 Endpoints da API

### Matérias-Primas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/raw-materials` | Listar todas |
| `GET` | `/api/raw-materials/{id}` | Buscar por ID |
| `POST` | `/api/raw-materials` | Cadastrar nova |
| `PUT` | `/api/raw-materials/{id}` | Atualizar |
| `DELETE` | `/api/raw-materials/{id}` | Remover |

### Produtos

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/products` | Listar todos |
| `GET` | `/api/products/{id}` | Buscar por ID |
| `POST` | `/api/products` | Cadastrar novo (com composição) |
| `PUT` | `/api/products/{id}` | Atualizar (redefine composição) |
| `DELETE` | `/api/products/{id}` | Remover |

### Otimização de Produção

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/production/optimize` | Calcular produção ótima |

<details>
<summary><b>Exemplo de resposta — POST /api/products</b></summary>

```json
{
  "code": "PRD001",
  "name": "Pão Francês",
  "price": 12.50,
  "compositions": [
    { "rawMaterialId": 1, "requiredQuantity": 200.0 },
    { "rawMaterialId": 2, "requiredQuantity": 50.0 }
  ]
}
```
</details>

<details>
<summary><b>Exemplo de resposta — GET /api/production/optimize</b></summary>

```json
[
  {
    "productCode": "PRD002",
    "productName": "Bolo de Chocolate",
    "quantity": 3,
    "unitPrice": 45.00,
    "totalValue": 135.00
  },
  {
    "productCode": "PRD001",
    "productName": "Pão Francês",
    "quantity": 5,
    "unitPrice": 12.50,
    "totalValue": 62.50
  }
]
```
</details>

> 📖 Documentação interativa completa disponível em **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testes

### Back-end — JUnit 6 + Mockito

```bash
cd backend
./mvnw test
```

| Arquivo de Teste | Cobertura |
|---|---|
| `ProductionOptimizerServiceTest` | 14 cenários (lista vazia, produto único, múltiplos, gargalo, conflito, borda) |
| `ProductServiceTest` | CRUD completo + composições |
| `RawMaterialServiceTest` | CRUD completo |
| `ProductControllerTest` | Endpoints REST |
| `ProductionControllerTest` | Endpoint de otimização |
| `RawMaterialControllerTest` | Endpoints REST |
| `GlobalExceptionHandlerTest` | Tratamento de erros |
| `ResourceNotFoundExceptionTest` | Exceção customizada |

### Front-end — Vitest

```bash
cd frontend
npm test
```

| Arquivo de Teste | Testes |
|---|---|
| `BaseTable.spec.js` | 7 — renderização, colunas, formatação, eventos edit/delete |
| `BaseModal.spec.js` | 4 — visibilidade, Teleport, eventos confirm/cancel |
| `RawMaterialForm.spec.js` | 5 — campos, initialData, validação, submit, cancel |
| `ProductComposition.spec.js` | 4 — estado vazio, renderização, adicionar/remover insumos |
| `rawMaterialStore.spec.js` | 5 — fetchAll, erro, create, update, remove |

> **Total: 25 testes front-end passando ✅**

---

## 📁 Estrutura do Projeto

```
teste_pratico_full_stack_PeD/
├── docker-compose.yml              # Orquestração (front + back)
├── README.md
│
├── backend/
│   ├── Dockerfile
│   ├── docker-compose.yml          # Backend standalone
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/java/com/example/backend/
│       │   ├── BackendApplication.java
│       │   ├── config/
│       │   │   ├── DataLoader.java          # Dados iniciais
│       │   │   ├── OpenApiConfig.java       # Swagger
│       │   │   ├── SecurityConfig.java      # CORS
│       │   │   └── WebPafhConfiguration.java
│       │   ├── controller/
│       │   │   ├── ProductController.java
│       │   │   ├── ProductionController.java
│       │   │   └── RawMaterialController.java
│       │   ├── dto/
│       │   │   ├── ProductDTO.java
│       │   │   ├── ProductCompositionDTO.java
│       │   │   ├── ProductionSuggestionDTO.java
│       │   │   └── RawMaterialDTO.java
│       │   ├── entity/
│       │   │   ├── Product.java
│       │   │   ├── ProductComposition.java
│       │   │   └── RawMaterial.java
│       │   ├── exception/
│       │   ├── repository/
│       │   └── service/
│       │       ├── ProductService.java
│       │       ├── ProductionOptimizerService.java  # Algoritmo Greedy
│       │       └── RawMaterialService.java
│       └── test/                    # 8 classes de teste
│
└── frontend/
    ├── Dockerfile
    ├── nginx.conf                   # SPA + proxy reverso
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.js                  # Bootstrap (Pinia + Router + i18n)
        ├── App.vue                  # Layout (Header + Sidebar + Router + Footer)
        ├── i18n/                    # Internacionalização
        │   ├── index.js
        │   └── locales/
        │       ├── pt-BR.json
        │       └── en.json
        ├── router/index.js          # 8 rotas
        ├── services/                # Camada HTTP (Axios)
        ├── stores/                  # Estado global (Pinia)
        ├── utils/                   # Formatação + validação
        ├── components/
        │   ├── common/              # 8 componentes base reutilizáveis
        │   ├── layout/              # Header, Sidebar, Footer
        │   ├── rawMaterial/         # Tabela + Formulário
        │   ├── product/             # Tabela + Formulário + Composição
        │   └── production/          # StockOverview + ProductionResult
        ├── views/                   # 6 páginas
        └── __tests__/               # 25 testes unitários
```

---

## 📸 Screenshots

### Dashboard
> Página inicial com visão geral de matérias-primas, produtos e ações rápidas.

### CRUD de Matérias-Primas
> Listagem com edição/exclusão inline e formulário com validação.

### CRUD de Produtos
> Formulário com composição dinâmica — adicione/remova insumos com quantidade.

### Otimização de Produção
> Visão do estoque atual → Botão "Calcular" → Resultado com produtos sugeridos e valor total.

### Internacionalização
> Troque entre 🇧🇷 Português e 🇺🇸 English no header da aplicação.

---

## 📄 Licença

Este projeto foi desenvolvido como parte de um **teste prático de desenvolvimento Full Stack** para a equipe de P&D.

---

<div align="center">

Feito com ☕ Java + 💚 Vue.js

</div>
