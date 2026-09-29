pipeline {
    agent any

    tools {
        // Nomes configurados em: Manage Jenkins > Tools
        // (JDK21 -> /usr/lib/jvm/java-21-openjdk-amd64, Maven -> /usr/share/maven)
        maven 'Maven'
        jdk   'JDK21'
    }

    // Dispara o pipeline quando o GitHub envia o webhook (evento push)
    triggers {
        githubPush()
    }

    options {
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Testes') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Empacotar') {
            steps {
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success { echo 'Pipeline finalizado com sucesso.' }
        failure { echo 'Pipeline falhou. Verifique os logs.' }
    }
}
