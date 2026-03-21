pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean test'
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
            archiveArtifacts artifacts: 'build/reports/**', allowEmptyArchive: true
        }
    }
}