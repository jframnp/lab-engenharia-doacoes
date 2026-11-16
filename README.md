# DPB: Doe Para O Bem

Plataforma web que conecta **doadores** e **beneficiários** (pessoas físicas e ONGs) de uma comunidade local para doar itens em bom estado: roupas, móveis, eletrônicos, livros e outros.

> Projeto da disciplina **Laboratório de Engenharia de Software**, Universidade Presbiteriana Mackenzie, 2026/2
> **Grupo MackLee:** Camila Huang · Edson Fu · Felipe Hideki Rodrigues Shinozaki · João Francisco · Xuanjun Jin

---

## Status da Sprint 1

| Requisito | Caso de uso | Back-end | Front-end | Testes |
|---|---|:-:|:-:|:-:|
| RF01: Cadastro de usuários (Doador / Beneficiário / ONG) | — | ✅ | ✅ | ✅ |
| RF02: Cadastro de itens com foto | **UC01: Cadastrar Item** | ✅ | ✅ | ✅ |
| RF03: Busca por categoria e região | UC04: Buscar Itens | ✅ | ✅ | ✅ |
| RF04: Solicitar item | **UC05: Solicitar Item** | ✅ | ✅ | ✅ |
| RF05 / RF06: Ver e aceitar/recusar solicitações | **UC06: Responder Solicitação** | ✅ | ✅ | ✅ |
| RF10: Aprovar beneficiário (ONG) | UC13: Aprovar Beneficiário | ✅ | ✅ | ✅ |
| RF12: Notificações de status | — | 🟡 básico | 🟡 básico | ✅ |
| RNF02: Senhas com hash (bcrypt) + JWT | — | ✅ | ✅ | ✅ |
| RNF01: Interface responsiva | — | — | ✅ | — |
| RF07, RF08, RF09, RF11, RF13, RF14 | UC07, UC10… | ⏳ próximas sprints | | |

Detalhes em [`docs/casos-de-uso.md`](docs/casos-de-uso.md) e no quadro [`docs/kanban.md`](docs/kanban.md).

## Arquitetura

MVC + arquitetura em camadas, conforme a seção 6 do documento do projeto:

```
frontend/  (View)        React + Vite + React Router
   │  HTTP/JSON (REST, JWT no header Authorization)
backend/
 ├─ routes/ + controllers/   Aplicação (Controller): recebe a requisição e delega
 ├─ services/                Negócio (Model): regras dos casos de uso
 ├─ dtos/                    DTO: validação e formato dos dados entre camadas
 ├─ repositories/            Persistência (Repository/DAO)
 │    ├─ memory/             implementação em memória (dev e testes)
 │    └─ postgres/           implementação PostgreSQL
 └─ container.js             composition root (injeção de dependências)
```

Mais detalhes em [`docs/arquitetura.md`](docs/arquitetura.md).

## Como rodar

### No GitHub Codespaces (sem instalar nada)

1. No repositório: **Code → Codespaces → Create codespace on main**. A instalação das dependências roda sozinha.
2. Abra um terminal e rode `cd backend && npm run dev`.
3. Abra um **segundo** terminal (botão `+`) e rode `cd frontend && npm run dev`.
4. O navegador abre o front-end. Se não abrir: aba **Ports** → porta `5173` → ícone 🌐.

### Na sua máquina

Pré-requisito: **Node.js 18+**.

### 1. Back-end (API em `http://localhost:3333/api`)

```bash
cd backend
npm install
cp .env.example .env      # no Windows: copy .env.example .env
npm run dev
```

Por padrão a API roda com `DB_DRIVER=memory`, **sem precisar de banco**, já com dados de demonstração.

Para usar **PostgreSQL**:

```bash
docker compose up -d          # na raiz do projeto
# no backend/.env: DB_DRIVER=postgres
cd backend
npm run db:migrate
npm run db:seed
npm run dev
```

### 2. Front-end (`http://localhost:5173`)

```bash
cd frontend
npm install
npm run dev
```

### Contas de demonstração (senha `123456`)

| Email | Perfil |
|---|---|
| `doador@dpb.com` | Doador, já com 4 itens cadastrados |
| `beneficiario@dpb.com` | Beneficiário pessoa física |
| `ong@dpb.com` | ONG **pendente** de aprovação |
| `admin@dpb.com` | Administrador |

### Testes

```bash
cd backend
npm test        # Jest + Supertest, com relatório de cobertura
```

## CI/CD (Jenkins na AWS)

Cada push na `main` dispara, via webhook do GitHub, o pipeline do [`Jenkinsfile`](Jenkinsfile), que roda em uma EC2 da AWS: testes do back-end (relatório JUnit) e build do front-end.
Instruções de setup da instância, do job e do webhook: [`docs/jenkins.md`](docs/jenkins.md).

## Endpoints principais

| Método | Rota | Quem | Descrição |
|---|---|---|---|
| POST | `/api/auth/registro` | público | Cadastro (RF01) |
| POST | `/api/auth/login` | público | Login, devolve JWT |
| GET | `/api/auth/me` | logado | Usuário atual |
| GET | `/api/itens?categoria=&cidade=&bairro=&q=` | público | Busca (RF03) |
| GET | `/api/itens/:id` | público | Detalhes do item |
| POST | `/api/itens` (multipart, campo `foto`) | doador | Cadastrar item (UC01) |
| GET | `/api/itens/meus` | doador | Itens do doador |
| POST | `/api/itens/:id/solicitacoes` | beneficiário | Solicitar item (UC05) |
| GET | `/api/solicitacoes/recebidas?status=` | doador | Solicitações recebidas (RF05) |
| PATCH | `/api/solicitacoes/:id/aceitar` | doador | Aceitar (UC06) |
| PATCH | `/api/solicitacoes/:id/recusar` | doador | Recusar (UC06/FA01) |
| GET | `/api/solicitacoes/minhas` | beneficiário | Acompanhar solicitações |
| GET | `/api/notificacoes` | logado | Notificações |
| GET | `/api/admin/beneficiarios/pendentes` | admin | Cadastros pendentes (UC13) |
| PATCH | `/api/admin/beneficiarios/:id/aprovar` | admin | Aprovar |
| PATCH | `/api/admin/beneficiarios/:id/rejeitar` | admin | Rejeitar com motivo |

## Fluxo de trabalho (Git)

- `main`: versão estável
- `develop`: integração da sprint
- `feature/<id>-<descricao>`: uma branch por tarefa do Kanban (ex.: `feature/rf07-combinar-retirada`)
- Commits no padrão [Conventional Commits](https://www.conventionalcommits.org/pt-br/): `feat:`, `fix:`, `docs:`, `test:`, `chore:`
- Pull Request para `develop` com ao menos 1 revisão

## Prompts utilizados (IA generativa)

O projeto foi desenvolvido com apoio de uma ferramenta de IA (Claude Code). Esta seção registra os prompts usados, na ordem em que foram enviados, para dar transparência ao processo.

| # | Etapa | Prompt (resumo) |
|---|-------|-----------------|
| 1 | **Back-end base + Arquitetura** | "Crie a estrutura inicial de um backend Node.js + Express seguindo arquitetura em camadas (Controller → Service → Repository). Use injeção de dependência via um `container.js`. Preciso de suporte a dois drivers de banco: `memory` (para desenvolvimento e testes) e `postgres`. Implemente autenticação com JWT + bcrypt. Explique cada camada e por que ela existe." |
| 2 | **Casos de uso principais** | "Implemente os casos de uso UC01 (Cadastrar Item com upload de foto), UC05 (Solicitar Item) e UC06 (Responder Solicitação) seguindo rigorosamente as regras de negócio e fluxos alternativos do documento do projeto. Use DTOs para validação. No aceite de solicitação, use transação no PostgreSQL. Explique as decisões de modelagem e os fluxos alternativos implementados." |
| 3 | **Front-end** | "Crie o frontend em React + Vite + React Router com as páginas necessárias para a Sprint 1 (login, cadastro, listagem/busca de itens, detalhes do item, cadastrar item, solicitações recebidas e minhas solicitações, área admin). Use context para autenticação com JWT. Faça a interface responsiva. Explique a estrutura de pastas e como o fluxo de autenticação funciona." |
| 4 | **Testes** | "Escreva testes unitários e de integração (Jest + Supertest) cobrindo os services e os principais endpoints da Sprint 1. Use o repositório em memória. Foque nas regras de negócio e nos fluxos alternativos (FA01, FA02 etc.). Mostre a cobertura e explique o que cada suite está validando." |
| 5 | **Infra / DevOps** | "Configure o projeto para rodar facilmente em GitHub Codespaces (devcontainer) e crie um Jenkinsfile que rode os testes do backend e o build do frontend em uma EC2 na AWS. Explique o pipeline e como configurar o webhook." |
