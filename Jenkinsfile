pipeline {
    agent any

    environment {
        TZ = 'Asia/Seoul'
        IMAGE_NAME = 'travel-journal-backend'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

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
                sh 'date'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .'
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