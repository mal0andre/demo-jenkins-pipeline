pipeline {
    agent any

    // Ajout d'un outil Maven nommé 'M3'. Assurez-vous qu'il existe dans la configuration globale Jenkins
    tools { maven 'M3' }

    environment {
        // placer le repo maven local dans le workspace pour réutilisation entre builds
        MAVEN_OPTS = "-Dmaven.repo.local=${WORKSPACE}/.m2/repository"
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins fournit automatiquement SCM
                checkout scm
            }
        }

        // Nouveau stage de diagnostic pour vérifier la disponibilité de mvn
        stage('Diag') {
            steps {
                script {
                    def mvnHome = tool 'M3'
                    if (isUnix()) {
                        sh "echo 'Maven home: ${mvnHome}' && echo 'PATH=$PATH' && ${mvnHome}/bin/mvn -v || true"
                    } else {
                        bat "echo Maven home: ${mvnHome} && echo %PATH% && \"${mvnHome}\\bin\\mvn\" -v || echo mvn-not-found"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Utiliser explicitement l'outil Maven configuré
                    def mvnHome = tool 'M3'
                    if (isUnix()) {
                        sh "${mvnHome}/bin/mvn -B -DskipTests clean package"
                    } else {
                        bat "\"${mvnHome}\\bin\\mvn\" -B -DskipTests clean package"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool 'M3'
                    if (isUnix()) {
                        sh "${mvnHome}/bin/mvn -B test"
                    } else {
                        bat "\"${mvnHome}\\bin\\mvn\" -B test"
                    }
                }
            }
            post {
                always {
                    // Publier les rapports JUnit (surefire). allowEmptyResults true évite d'échouer si aucun rapport trouvé.
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline réussi : build et tests OK'
        }
        failure {
            echo 'Pipeline échoué : voir les logs et résultats de tests'
        }
        always {
            cleanWs()
        }
    }
}
