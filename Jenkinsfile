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

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'chmod +x ./scripts/deploy-main.sh'
                sh './scripts/deploy-main.sh ${IMAGE_TAG}'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
            archiveArtifacts artifacts: 'build/reports/**', allowEmptyArchive: true
        }
    }
}