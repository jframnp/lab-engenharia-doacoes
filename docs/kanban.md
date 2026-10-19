# Kanban: Sprint 1

Quadro usado no **GitHub Projects** (colunas: *Backlog → A fazer → Em andamento → Em revisão → Concluído*).
Cada card abaixo vira uma *issue* com a label indicada. Os responsáveis são sugestões; ajustem conforme a divisão do grupo.

**Objetivo da sprint:** fluxo principal de doação funcionando de ponta a ponta (cadastrar → doar → buscar → solicitar → responder).

## ✅ Concluído

| # | Card | Requisito | Label | Responsável |
|---|---|---|---|---|
| 1 | Criar repositório, README e estrutura `backend/` + `frontend/` | — | `chore` | João |
| 2 | Configurar Express, camadas (controller/service/repository) e tratamento de erros | RNF04 | `backend` | João |
| 3 | Modelar banco (schema.sql) e docker-compose do PostgreSQL | — | `backend` `banco` | Edson |
| 4 | Cadastro de usuário + login com JWT e bcrypt | RF01, RNF02 | `backend` | Felipe |
| 5 | Telas de Cadastro e Login | RF01 | `frontend` | Camila |
| 6 | API de cadastro de item com upload de foto | RF02 / UC01 | `backend` | Xuanjun |
| 7 | Tela "Doar: Cadastrar Item" | RF02 / UC01 | `frontend` | Camila |
| 8 | API de busca por categoria/região | RF03 | `backend` | Edson |
| 9 | Página inicial com busca e filtros | RF03, RNF01 | `frontend` | Camila |
| 10 | API Solicitar Item (FA01, FA02) | RF04 / UC05 | `backend` | Felipe |
| 11 | Página de detalhes do item + formulário de solicitação | RF04 / UC05 | `frontend` | Xuanjun |
| 12 | API aceitar/recusar solicitação (transação + recusa automática) | RF05, RF06 / UC06 | `backend` | João |
| 13 | Tela "Pedidos: Solicitações Recebidas" | RF05, RF06 / UC06 | `frontend` | Xuanjun |
| 14 | Testes unitários e de integração da Sprint 1 | RNF07 | `teste` | Edson |

## 🔄 Em andamento

| # | Card | Requisito | Label | Responsável |
|---|---|---|---|---|
| 15 | Aprovação de ONGs pelo administrador (API + tela pronta; falta upload de documentos) | RF10 / UC13 | `backend` `frontend` | Felipe |
| 16 | Notificações: hoje internas; falta envio por email | RF12 | `backend` | João |
| 17 | Pipeline Jenkins na EC2 (AWS) acionado por webhook do GitHub | RNF07 | `chore` | Edson |

## 📋 A fazer (Sprint 2)

| # | Card | Requisito | Label |
|---|---|---|---|
| 18 | Combinar retirada entre doador e beneficiário (UC07: Concluir Doação) | RF07 | `backend` `frontend` |
| 19 | Página "Histórico de Doações" | RF08 | `frontend` |
| 20 | Moderação: admin remove item impróprio | RF11 | `backend` `frontend` |
| 21 | Editar e remover item pelo doador | RF02 | `backend` `frontend` |
| 22 | Documentação da API com Swagger/OpenAPI | RNF04 | `docs` |
| 23 | Trocar storage local por Cloudinary/S3 | — | `backend` |

## 🗂️ Backlog

| Card | Requisito |
|---|---|
| Selos de gamificação para doadores frequentes | RF09 |
| Avaliar usuário após a doação (UC10) | RF13 |
| Exportar histórico em PDF | RF14 |
| Termos de uso e consentimento LGPD | RNF06 |
| Internacionalização (pt-BR padrão) | RNF08 |
| Deploy (Render/Railway + Vercel) | RNF05 |

---

### Como montar o quadro no GitHub

1. No repositório: **Projects → New project → Board**.
2. Renomeie as colunas para *Backlog, A fazer, Em andamento, Em revisão, Concluído*.
3. Crie as labels: `backend`, `frontend`, `banco`, `teste`, `docs`, `chore`.
4. Para cada linha acima, crie uma issue (título = card, corpo = requisito/caso de uso) e adicione ao projeto na coluna correspondente.
5. Crie a milestone **Sprint 1** e associe os cards 1–17.
