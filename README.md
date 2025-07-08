 - Nome do sistema: Valoris
 - Integrantes do grupo: Giovanna Batista, Pedro Vinícius e Wisla Argolo (respectivos usuários no Github: giovvbat, pedropinho60, wislaargolo)
 - Linguagem de Programação e Stack de desenvolvimento: Java com Spring Boot

## Compilação do projeto

Para compilar o projeto, devem ser instaladas as seguintes dependências:
 - Java 17
 - Maven

Após a instalação, o projeto pode ser compilado e executado através do comando:
```bash
mvn spring-boot:run
```
Esse comando deve ser executado na pasta raiz do projeto, onde se encontra o arquivo `pom.xml`.

# API Valoris
Link repositório: https://github.com/giovvbat/valoris.git

Link imagem Docker: https://hub.docker.com/r/giovvbatista/valoris/tags

> De modo geral, as regras e endpoints apresentados consideram a branch *production*.

## Base URL

```
/banco/conta
```

---

## Endpoints

### 1. Criar Conta

**POST** 

#### Request Body

```json
{
  "number": "12345",
  "balance": 100.0,
  "type": "POUPANCA"
}
```

> *number* obrigatório <br>
> *balance* obrigatório para contas do tipo PADRAO e POUPANCA <br>
> *type* obrigatório e pode ser PADRAO, BONUS ou POUPANCA 
#### Response

```json
{
  "number": "12345",
  "balance": 100.0,
  "type": "POUPANCA"
}
```


---

### 2. Consultar Conta

**GET** `/{id}`

#### Path Variable

- `id`: número da conta 

#### Response

```json
{
  "number": "12345",
  "balance": 100.0,
  "type": "POUPANCA"
}
```
---

### 3. Consultar Saldo

**GET** `/{id}/saldo`

#### Path Variable

- `id`: número da conta 

#### Response

```json
{
  "number": "12345",
  "balance": 100.0
}
```

---

### 4. Listar Contas

**GET** 


#### Response

```json
[
    {
        "number": "12345",
        "balance": 100.0,
        "type": "POUPANCA"
    },
    {
        "number": "67891",
        "balance": 0.0,
        "type": "BONUS",
        "pontuation": 10
    }
]
```

---

### 5. Debitar Conta

**PUT** `/{id}/debito`

#### Path Variable

- `id`: número da conta

#### Request Body

```json
{
  "amount": 20.0
}
```
> *amount* obrigatório e não deve ser negativo<br>
> Em *production*, saldo após essa operação não pode ser negativo. 

#### Response

```json
{
  "number": "12345",
  "balance": 80.0,
  "type": "POUPANCA"
}
```
---

### 6. Creditar Conta

**PUT** `/{id}/credito`

#### Path Variable

- `id`: número da conta

#### Request Body

```json
{
  "amount": 200.0
}
```
> *amount* obrigatório e não deve ser negativo

#### Response

```json
{
  "number": "12345",
  "balance": 280.0,
  "type": "POUPANCA"
}
```

---

### 7. Transferência entre Contas

**PUT** `/transferencia`

#### Request Body

```json
{
  "from": "12345",
  "to": "67891",
  "amount": 20.0
}
```

> *from* obrigatório<br>
> *to* obrigatório<br>
> *amount* obrigatório e não deve ser negativo<br>
> Em *production*, saldo após essa operação não pode ser negativo.

#### Response

```json
[
    {
      "number": "12345",
      "balance": 80.0,
      "type": "POUPANCA"
    },
    {
      "number": "67891",
      "balance": 20.0,
      "type": "BONUS",
      "pontuation": 10
    }
]
```

> O resultado retorna a **conta de origem** e **conta de destino** após a transferência.

### 8. Render Juros

**PUT** `/rendimento`

#### Request Body

```json
{
  "tax": 10.5
}
```

> *tax* obrigatório e deve ser positivo<br>

#### Response

```json
[
    {
      "number": "12345",
      "balance": 287.3,
      "type": "POUPANCA"
    }
]
```

> O resultado retorna as **contas do tipo poupança** com saldo atualizados.

