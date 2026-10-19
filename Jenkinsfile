pipeline {
    agent any

    // A VM do Jenkins (aws/user-data.sh) tem só Java e Maven.
    // O próprio pipeline baixa o Node.js para dentro do workspace
    // na primeira execução e reaproveita nos builds seguintes.
    environment {
        NODE_VERSION = '20.18.0'
        NODE_DIST    = "node-v${NODE_VERSION}-linux-x64"
    }

    // Dispara o pipeline quando o GitHub envia o webhook (evento push)
    triggers {
        githubPush()
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Preparar Node.js') {
            steps {
                sh '''
                    if [ ! -x ".node/${NODE_DIST}/bin/node" ]; then
                        mkdir -p .node
                        curl -fsSL "https://nodejs.org/dist/v${NODE_VERSION}/${NODE_DIST}.tar.gz" \
                          | tar -xz -C .node
                    fi
                    .node/${NODE_DIST}/bin/node -v
                '''
            }
        }

        stage('Back-end: testes') {
            steps {
                withEnv(["PATH+NODE=${env.WORKSPACE}/.node/${env.NODE_DIST}/bin"]) {
                    dir('backend') {
                        sh 'npm ci --no-audit --no-fund'
                        sh 'npm test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/reports/junit.xml'
                }
            }
        }

        stage('Front-end: build') {
            steps {
                withEnv(["PATH+NODE=${env.WORKSPACE}/.node/${env.NODE_DIST}/bin"]) {
                    dir('frontend') {
                        sh 'npm ci --no-audit --no-fund'
                        sh 'npm run build'
                    }
                }
            }
        }

        stage('Empacotar') {
            steps {
                archiveArtifacts artifacts: 'frontend/dist/**', fingerprint: true
            }
        }
    }

    post {
        success { echo 'Pipeline finalizado com sucesso.' }
        failure { echo 'Pipeline falhou. Verifique os logs.' }
    }
}
