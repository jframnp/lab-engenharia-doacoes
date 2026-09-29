# DPB

Projeto Java 21 (Maven + JUnit 5) com build e testes automatizados no Jenkins (EC2 na AWS),
disparados por webhook do GitHub. Roteiro baseado no *Laboratório 7 – CI/CD - Jenkins*.

## Estrutura

```
dpb/
├── .devcontainer/devcontainer.json
├── aws/user-data.sh                      <- script de instalação do Jenkins na EC2
├── src/
│   ├── main/java/com/dpb/model/Item.java
│   └── test/java/com/dpb/model/ItemTest.java
├── .gitignore
├── Jenkinsfile                           <- usado só na opção B (job Pipeline)
├── pom.xml
└── README.md
```

## Rodar localmente

```
mvn clean install
```

Resultado esperado: `Tests run: 5, Failures: 0, Errors: 0` e `target/dpb-1.0.0.jar`.

---

## 1. Subir o projeto para o GitHub

Crie um repositório **público** no GitHub (ex.: `dpb-projeto`), sem README, e na pasta do projeto:

```
git init
git add .
git commit -m "Projeto DPB com testes"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/dpb-projeto.git
git push -u origin main
```

## 2. Criar a instância EC2 (AWS Academy)

EC2 > **Launch instance**:

| Campo | Valor |
|---|---|
| Name | `Jenkins Lab` |
| AMI | Ubuntu Server 24.04 |
| Instance type | `t3.small` |
| Key pair | `vockey` |
| Network | VPC padrão (default) |
| Firewall | Create security group: `Jenkins Lab Security Group` / `Security Group Jenkins Lab` (deixe marcado *Allow SSH*) |
| Advanced details > User data | cole o conteúdo inteiro de [`aws/user-data.sh`](aws/user-data.sh) |

Clique em **Launch instance** e aguarde o status *Running* e *2/2 checks passed*.
A instalação do Jenkins continua rodando por uns 3–5 minutos depois disso.

## 3. Liberar a porta 8080

Instância > aba **Security** > clique no Security Group `Jenkins Lab Security Group` >
**Inbound rules** > **Edit inbound rules** > **Add rule**:

- Type: `Custom TCP`
- Port: `8080`
- Source: `Anywhere-IPv4` (0.0.0.0/0)

Salve.

## 4. Acessar a instância por SSH

Baixe a chave em AWS Academy > *AWS Details* > *Download PEM* (`labsuser.pem`). No terminal (Git Bash):

```
mkdir -p ~/.ssh && mv ~/Downloads/labsuser.pem ~/.ssh/
chmod 400 ~/.ssh/labsuser.pem
ssh -i ~/.ssh/labsuser.pem ubuntu@<public-ip>
```

`<public-ip>` = instância > Details > *Public IPv4 address*.

Confira se a instalação terminou:

```
sudo tail -n 20 /var/log/jenkins-bootstrap.log   # deve mostrar "active (running)", versão do Java 21 e do Maven
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

## 5. Configuração inicial do Jenkins

Abra `http://<public-ip>:8080`:

1. **Unlock Jenkins**: cole a senha do `initialAdminPassword`.
2. **Install suggested plugins**.
3. Crie o usuário administrador.
4. **Instance Configuration**: Jenkins URL = `http://<public-ip>:8080/` > *Save and Finish*.
5. **Manage Jenkins > Plugins > Available plugins**: pesquise `Maven Integration`, marque e instale. Reinicie o Jenkins ao final.

## 6. Configurar as ferramentas (Manage Jenkins > Tools)

Desmarque *Install automatically* nos dois:

| Seção | Name | Caminho |
|---|---|---|
| JDK installations > Add JDK | `JDK21` | JAVA_HOME = `/usr/lib/jvm/java-21-openjdk-amd64` |
| Maven installations > Add Maven | `Maven` | MAVEN_HOME = `/usr/share/maven` |

Clique em **Save**. Os nomes precisam ser exatamente esses, pois o `Jenkinsfile` os usa.

## 7. Criar o job

### Opção A: Maven project (como no roteiro da aula)

**New Item** > nome `dpb` > **Maven project** > OK.

- **General**: Description `Jenkins + Maven + GitHub`; marque *GitHub project* > Project url `https://github.com/SEU_USUARIO/dpb-projeto/`
- **Source Code Management**: Git > Repository URL `https://github.com/SEU_USUARIO/dpb-projeto.git`; Branch Specifier `*/main`
- **Build Triggers**: marque *GitHub hook trigger for GITScm polling*
- **Build**: Root POM `pom.xml`; Goals and options `clean install`
  - *Advanced...* > MAVEN_OPTS: `-Dmaven.test.failure.ignore=false` (se um teste falhar, o build falha)
- **Save** e clique em **Build Now**.

### Opção B: Pipeline (usa o `Jenkinsfile` do repositório)

**New Item** > nome `dpb-pipeline` > **Pipeline** > OK.

- *GitHub project* > Project url do repositório
- *Build Triggers*: marque *GitHub hook trigger for GITScm polling*
- *Pipeline*: Definition `Pipeline script from SCM` > SCM `Git` > URL do repositório > Branch `*/main` > Script Path `Jenkinsfile`
- **Save** > **Build Now**. Os estágios são Checkout, Build, Testes (relatório JUnit) e Empacotar (arquiva o `.jar`).

No *Console Output* deve aparecer `Tests run: 5, Failures: 0` e `BUILD SUCCESS`.

## 8. Webhook no GitHub

Repositório > **Settings > Webhooks > Add webhook**:

- Payload URL: `http://<public-ip>:8080/github-webhook/` (a barra final é obrigatória)
- Content type: `application/json`
- Evento: *Just the push event*
- Active: marcado > **Add webhook**

O ícone deve ficar com ✓ verde (em *Recent Deliveries*, resposta 200).

## 9. Testar a automação

Altere algo no código, faça `git commit` e `git push`. Um novo build deve iniciar sozinho no Jenkins.
Para ver a falha acontecendo, quebre um teste de propósito (ex.: troque `"DISPONIVEL"` por `"X"` em
`testStatusInicial`), faça push e veja o build ficar vermelho. Depois desfaça.

---

## Problemas comuns

- **`No compiler is provided in this environment`** ou **"não parece um diretório JDK"**: foi instalado só o JRE.
  Rode `sudo apt-get install -y openjdk-21-jdk` na instância (o `aws/user-data.sh` já instala o JDK).
- **Jenkins não abre na 8080**: confira a regra de entrada do Security Group e `sudo systemctl status jenkins`.
- **Webhook com erro / timeout**: o IP público da instância **muda sempre que o lab da AWS Academy é reiniciado**.
  Atualize a Payload URL do webhook e a *Jenkins URL* (Manage Jenkins > System).
- **Build não dispara no push**: rode o job manualmente uma vez antes. O trigger só é registrado após o primeiro build.
