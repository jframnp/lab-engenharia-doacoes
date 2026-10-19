#!/bin/bash
# User Data da instância EC2 "Jenkins Lab" (Ubuntu Server 24.04, t3.small).
# Cole este conteúdo em: Launch instance > Advanced details > User data.
# Log da execução: /var/log/jenkins-bootstrap.log
set -euo pipefail
export DEBIAN_FRONTEND=noninteractive

# Registra a execução para consulta na instância
exec > >(tee -a /var/log/jenkins-bootstrap.log) 2>&1

# Dependências, Java e Maven
# Obs.: usamos o JDK (openjdk-21-jdk) e não só o JRE, porque o Maven precisa
# do javac para compilar o projeto e o Jenkins só aceita um JAVA_HOME de JDK.
apt-get update
apt-get install -y \
  ca-certificates \
  curl \
  gnupg \
  fontconfig \
  git \
  openjdk-21-jdk \
  maven

# Repositório oficial Jenkins LTS
install -d -m 0755 /etc/apt/keyrings
curl -fsSL \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key \
  -o /etc/apt/keyrings/jenkins-keyring.asc

echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] \
https://pkg.jenkins.io/debian-stable binary/" \
  > /etc/apt/sources.list.d/jenkins.list

# Instala a versão LTS atual do Jenkins
apt-get update
apt-get install -y jenkins

# Habilita e inicia o serviço
systemctl enable --now jenkins

# Aguarda o Jenkins ficar ativo
for i in {1..12}; do
  if systemctl is-active --quiet jenkins; then
    break
  fi
  sleep 5
done

# Exibe informações úteis no log de User Data
systemctl status jenkins --no-pager
dpkg -s jenkins | grep Version
java -version
mvn -version
