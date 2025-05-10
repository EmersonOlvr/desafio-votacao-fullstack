# Sistema de Sessões de Votação

Este projeto é uma solução full-stack para iniciar e gerenciar sessões de votação em pautas específicas. Ele é composto por:

- **Frontend**: React + TypeScript com Material UI.
- **Backend**: Spring Boot (Java 21).
- **Banco de Dados**: PostgreSQL.
- **Containerização**: Docker + Docker Compose.

---

## 🚀 Tecnologias Utilizadas

### Frontend

- React + TypeScript
- Material UI
- React Hook Form
- Axios
- Notistack para notificações

### Backend

- Java 21 + Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Swagger para documentação da API

### Outros

- Docker & Docker Compose

---

## 💡 Decisões de Arquitetura

- **Separação clara de responsabilidades**: A estrutura em frontend e backend permite organização, manutenibilidade e escalabilidade.
- **Spring Boot no backend**: Por ser uma escolha madura, produtiva e escalável para APIs RESTful, com fácil integração ao ecossistema Java.
- **Docker**: Facilita o setup do ambiente, isolando dependências e garantindo consistência entre diferentes ambientes.
- **React Hook Form**: Garante ótima performance e controle sobre formulários no frontend.
- **Material UI**: Usado para consistência visual, responsividade e agilidade no desenvolvimento da interface com uma biblioteca rica em componentes e responsiva.

---

## ✅ Requisitos

- Docker e Docker Compose instalados
- Git instalado

---

## ⚙️ Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/EmersonOlvr/desafio-votacao-fullstack.git
cd desafio-votacao-fullstack
```

### 2. Rode o projeto com Docker Compose

```bash
docker compose up --build
```

### 3. Acesse as aplicações

- Frontend: [http://localhost:3000](http://localhost:3000)
- Backend (API): [http://localhost:8085](http://localhost:8085)
- Swagger UI (Documentação da API): [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)

---


# Votação

## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução we para gerenciar e participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST / Front:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
  um tempo determinado na chamada de abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
  é identificado por um id único e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser considerada como autorizada. A solução deve ser construída em java com Spring-boot e Angular/React conforme orientação, mas os frameworks e bibliotecas são de livre escolha (desde que não infrinja direitos de uso).

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

## Como proceder

Por favor, realize o FORK desse repositório e implemente sua solução no FORK em seu repositório GItHub, ao final, notifique da conclusão para que possamos analisar o código implementado.

Lembre de deixar todas as orientações necessárias para executar o seu código.

### Tarefas bônus

- Tarefa Bônus 1 - Integração com sistemas externos
  - Criar uma Facade/Client Fake que retorna aleátoriamente se um CPF recebido é válido ou não.
  - Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos
  - Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação. Essa operação retorna resultados aleatórios, portanto um mesmo CPF pode funcionar em um teste e não funcionar no outro.

```
// CPF Ok para votar
{
    "status": "ABLE_TO_VOTE
}
// CPF Nao Ok para votar - retornar 404 no client tb
{
    "status": "UNABLE_TO_VOTE
}
```

Exemplos de retorno do serviço

### Tarefa Bônus 2 - Performance

- Imagine que sua aplicação possa ser usada em cenários que existam centenas de
  milhares de votos. Ela deve se comportar de maneira performática nesses
  cenários
- Testes de performance são uma boa maneira de garantir e observar como sua
  aplicação se comporta

### Tarefa Bônus 3 - Versionamento da API

○ Como você versionaria a API da sua aplicação? Que estratégia usar?

## O que será analisado

- Simplicidade no design da solução (evitar over engineering)
- Organização do código
- Arquitetura do projeto
- Boas práticas de programação (manutenibilidade, legibilidade etc)
- Possíveis bugs
- Tratamento de erros e exceções
- Explicação breve do porquê das escolhas tomadas durante o desenvolvimento da solução
- Uso de testes automatizados e ferramentas de qualidade
- Limpeza do código
- Documentação do código e da API
- Logs da aplicação
- Mensagens e organização dos commits
- Testes
- Layout responsivo

## Dicas

- Teste bem sua solução, evite bugs

  Observações importantes
- Não inicie o teste sem sanar todas as dúvidas
- Iremos executar a aplicação para testá-la, cuide com qualquer dependência externa e
  deixe claro caso haja instruções especiais para execução do mesmo
  Classificação da informação: Uso Interno



# desafio-votacao
