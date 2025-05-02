
# PowerCrm

## Descrição do Projeto  
O **PowerCrm** é um sistema de gerenciamento de veículos e usuários com integração à API FIPE, oferecendo funcionalidades como cadastro, consulta e atualização de dados de forma eficiente. O projeto usa uma arquitetura moderna com banco de dados relacional, cache, e mensageria com Kafka para processamento assíncrono de eventos.

## 🛠 Tecnologias Utilizadas  
- **Java 21**  
- **Spring Boot** (Spring Web, Data JPA, Validation, Cache, Kafka, Scheduling)  
- **Flyway** – Migrações de banco de dados  
- **MySQL** – Banco de dados relacional  
- **Kafka** – Mensageria para eventos assíncronos  
- **MapStruct** – Mapeamento de DTOs  
- **Lombok** – Redução de código repetitivo  
- **Docker / Docker Compose** – Ambientes isolados e reproduzíveis

---

## Como Iniciar o Projeto

### Pré-requisitos  
- Java 21  
- Maven  
- Docker e Docker Compose (opcional)  
- MySQL (se for executar localmente sem Docker)  

### Usando Docker  
1. Certifique-se de que **Docker** e **Docker Compose** estão instalados.  
2. No diretório raiz do projeto, execute:
   ```bash
   docker-compose up --build
   ```
3. O serviço estará disponível em:  
   [http://localhost:8080](http://localhost:8080)

---

### Executando Localmente (sem Docker)

1. **Crie o banco de dados** no MySQL:
   ```sql
   CREATE DATABASE powercrm;
   ```

2. **Configure as credenciais** no arquivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/powercrm?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=powercrm
   spring.datasource.password=powercrm
   ```

3. **Execute as migrações do banco de dados**:
   ```bash
   mvn flyway:migrate
   ```

4. **Inicie o projeto**:
   ```bash
   mvn spring-boot:run
   ```

5. O serviço estará disponível em:  
   [http://localhost:8080](http://localhost:8080)

---

## Configuração de Ambiente (.env)

Crie um arquivo `.env` com as seguintes variáveis para que o sistema funcione corretamente:

```env
# Flyway
FLYWAY_URL=jdbc:mysql://mysql:3306/powercrm?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
FLYWAY_USER=powercrm
FLYWAY_PASSWORD=powercrm

# DataSource
DATASOURCE_URL=jdbc:mysql://mysql:3306/powercrm?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATASOURCE_USERNAME=powercrm
DATASOURCE_PASSWORD=powercrm

# Token da API FIPE 
FIPE_API_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# MySQL
MYSQL_DATABASE=powercrm
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=powercrm
MYSQL_PASSWORD=powercrm
```
Ou configure diretamente em application.properties como prefirir

---

## Rodando os Testes

Para executar os testes automatizados do projeto, utilize:

```bash
mvn test
```

---

## Endpoints Principais

### Usuários  
- `POST /api/users` – Criar um novo usuário  
- `GET /api/users` – Listar usuários com filtros  
- `GET /api/users/{id}` – Buscar usuário por ID  
- `PUT /api/users/{id}` – Atualizar um usuário 
- `PUT /api/users/{id}/status` – Atualizar status de um usuário   
- `DELETE /api/users/{id}` – Deletar um usuário  

### Veículos  
- `POST /api/vehicle` – Criar um novo veículo  
- `GET /api/vehicle` – Listar veículos com filtros  
- `GET /api/vehicle/{id}` – Buscar veículo por ID  
- `PUT /api/vehicle/{id}` – Atualizar um veículo  
- `DELETE /api/vehicle/{id}` – Deletar um veículo  

## Postman

Uma collection do Postman está disponível para facilitar o teste das rotas da API.
Download da Collection: [Collection Postman - PowerCrm](https://www.postman.com/restless-escape-823255/workspace/onfly/collection/27431455-a00f66d6-bcf4-42f0-9bf2-9ff55867cc7f?action=share&creator=27431455)

---

## Observações  

- O sistema utiliza **cache** para otimizar consultas frequentes.  
- A sincronização com a **API FIPE** é feita automaticamente na inicialização e diariamente às **2h da manhã**.  
- Eventos de validação de veículos são processados de forma **assíncrona via Kafka**.


## ⚠️ Observação sobre a API FIPE

> Caso exceda o limite de requisições da API FIPE, será necessário fazer o cadastro em [https://fipeapi.com.br/](https://fipeapi.com.br/) e gerar um Token de Acesso.
> Configure o token no arquivo `application.properties` e descomente a linha correspondente à configuração da API em [FipeClient.java](src/main/java/com/service/powercrm/service/integracao/FipeClient.java).
