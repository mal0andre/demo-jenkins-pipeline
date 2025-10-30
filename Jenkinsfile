pipeline {
    agent any

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

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B -DskipTests clean package'
                    } else {
                        bat 'mvn -B -DskipTests clean package'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B test'
                    } else {
                        bat 'mvn -B test'
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
