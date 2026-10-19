# CI/CD com Jenkins (EC2 na AWS)

Cada `git push` na `main` dispara, via webhook do GitHub, o pipeline definido no [`Jenkinsfile`](../Jenkinsfile):

| Estágio | O que faz |
|---|---|
| Checkout | baixa o código do GitHub |
| Preparar Node.js | baixa o Node 20 para `.node/` no workspace (só no primeiro build; a VM não precisa ter Node instalado) |
| Back-end: testes | `npm ci` + `npm test` (Jest). O relatório JUnit aparece em *Test Result* no Jenkins |
| Front-end: build | `npm ci` + `npm run build` (Vite) |
| Empacotar | arquiva `frontend/dist/` como artefato do build |

Se um teste falhar, o build fica vermelho.

## Setup da instância (resumo)

1. **EC2**: Ubuntu Server 24.04, `t3.small`, key pair `vockey`. Em *Advanced details > User data*, cole o [`aws/user-data.sh`](../aws/user-data.sh). Ele instala Git, Java 21 e Jenkins LTS.
2. **Security Group**: regra de entrada `Custom TCP 8080` liberada para `0.0.0.0/0`.
3. **Jenkins** em `http://<public-ip>:8080`: desbloqueie com `sudo cat /var/lib/jenkins/secrets/initialAdminPassword`, escolha *Install suggested plugins* e crie o usuário admin.

## Job

O job precisa ser do tipo **Pipeline**. Um job do tipo *Maven project* não lê o `Jenkinsfile`.

**New Item** > **Pipeline**:

- *GitHub project*: `https://github.com/jframnp/lab-engenharia-doacoes/`
- *Build Triggers*: marque **GitHub hook trigger for GITScm polling**
- *Pipeline*: `Pipeline script from SCM` > Git > `https://github.com/jframnp/lab-engenharia-doacoes.git` > Branch `*/main` > Script Path `Jenkinsfile`
- **Save** > **Build Now**. Rode uma vez manualmente para o trigger ser registrado.

## Webhook no GitHub

Repositório > **Settings > Webhooks > Add webhook**:

- Payload URL: `http://<public-ip>:8080/github-webhook/` (a barra final é obrigatória)
- Content type: `application/json`
- Evento: *Just the push event*

## Problemas comuns

- **Webhook com timeout**: o IP público **muda toda vez que o lab da AWS Academy reinicia**. Atualize a Payload URL e a *Jenkins URL* (Manage Jenkins > System).
- **`curl: (6) Could not resolve host nodejs.org`** no estágio *Preparar Node.js*: a instância está sem saída para a internet. Confira a VPC e o Security Group (regras de saída).
- **Build não dispara no push**: rode o job manualmente uma vez antes.
- **`npm ci` lento ou travando por memória**: na `t3.small` (2 GB) costuma funcionar. Se faltar memória, crie um swap de 1 GB na instância.
