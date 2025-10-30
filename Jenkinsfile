pipeline {
    agent {
        docker {
            // image Maven + OpenJDK 17
            image 'maven:3.8.8-openjdk-17'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

    environment {
        // optionnel : forcer le repo maven local dans le conteneur
        MAVEN_OPTS = "-Dmaven.repo.local=/root/.m2/repository"
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins fournit automatiquement SCM, on l'utilise
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Build sans tests pour accélérer l'étape, les tests seront lancés ensuite
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Test') {
            steps {
                // Lancer les tests JUnit (JUnit 5 est configuré dans pom.xml)
                sh 'mvn -B test'
            }
            post {
                // Toujours publier les rapports JUnit, même si échec
                always {
                    // Emplacement standard des rapports surefire
                    junit allowEmptyResults: false, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive') {
            steps {
                // Archive l'artefact (si présent)
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo "Pipeline réussi : build et tests OK"
        }
        failure {
            echo "Pipeline échoué : voir les logs et résultats de tests"
        }
        always {
            // Nettoyer workspace du conteneur
            cleanWs()
        }
    }
}

