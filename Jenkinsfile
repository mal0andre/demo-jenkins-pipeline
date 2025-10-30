pipeline {
    agent any

    // Paramètre pour simuler un échec de tests sans modifier le code source
    parameters {
        booleanParam(name: 'SIMULATE_FAILURE', defaultValue: false, description: 'Si true, crée un test temporaire qui échoue pour valider le comportement du pipeline en cas d\'échec.')
    }

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

                    // Si demandé, créer un test temporaire qui provoque un échec
                    if (params.SIMULATE_FAILURE) {
                        if (isUnix()) {
                            sh 'mkdir -p src/test/java'
                            sh 'cat > src/test/java/FailTest.java <<\'EOF\'\nimport org.junit.Test;\nimport static org.junit.Assert.fail;\npublic class FailTest {\n    @Test\n    public void failing() {\n        fail("Intentional failure for pipeline simulation");\n    }\n}\nEOF'
                        } else {
                            bat 'if not exist src\\test\\java mkdir src\\test\\java'
                            bat 'echo import org.junit.Test;> src\\test\\java\\FailTest.java'
                            bat 'echo import static org.junit.Assert.fail;>> src\\test\\java\\FailTest.java'
                            bat 'echo public class FailTest {>> src\\test\\java\\FailTest.java'
                            bat 'echo.    @Test>> src\\test\\java\\FailTest.java'
                            bat 'echo.    public void failing() {>> src\\test\\java\\FailTest.java'
                            bat 'echo.        fail("Intentional failure for pipeline simulation");>> src\\test\\java\\FailTest.java'
                            bat 'echo.    }>> src\\test\\java\\FailTest.java'
                            bat 'echo }>> src\\test\\java\\FailTest.java'
                        }
                    }

                    if (isUnix()) {
                        sh "${mvnHome}/bin/mvn -B test"
                    } else {
                        bat "\"${mvnHome}\\bin\\mvn\" -B test"
                    }

                    // Nettoyage du test temporaire si présent
                    if (params.SIMULATE_FAILURE) {
                        if (isUnix()) {
                            sh 'rm -f src/test/java/FailTest.java'
                        } else {
                            bat 'del /F /Q src\\test\\java\\FailTest.java || echo nofile'
                        }
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
