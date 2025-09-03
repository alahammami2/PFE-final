pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Prepare .env') {
      environment {
        // If you create Jenkins credentials with these IDs, they will be injected.
        // Otherwise defaults from .env.example will be used if present.
      }
      steps {
        script {
          def groq = ''
          def jwt = ''
          def dbp = ''
          def mailUser = ''
          def mailPass = ''
          try {
            withCredentials([
              string(credentialsId: 'groq-api-key', variable: 'CRED_GROQ'),
              string(credentialsId: 'jwt-secret', variable: 'CRED_JWT'),
              string(credentialsId: 'db-password', variable: 'CRED_DBP'),
              usernamePassword(credentialsId: 'mail-smtp', usernameVariable: 'CRED_MAIL_USER', passwordVariable: 'CRED_MAIL_PASS')
            ]) {
              groq = env.CRED_GROQ ?: ''
              jwt  = env.CRED_JWT  ?: ''
              dbp  = env.CRED_DBP  ?: ''
              mailUser = env.CRED_MAIL_USER ?: ''
              mailPass = env.CRED_MAIL_PASS ?: ''
            }
          } catch (ignored) {
            // Credentials not configured; will fall back to defaults
          }

          writeFile file: '.env', text: """
GROQ_API_KEY=${groq}
JWT_SECRET=${jwt}
DB_PASSWORD=${dbp}
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=${mailUser}
MAIL_PASSWORD=${mailPass}
"""
        }
      }
    }

    stage('Docker Compose Build & Up') {
      steps {
        sh 'docker compose version || docker-compose version'
        sh 'docker compose -f docker-compose.yml up -d --build'
      }
    }
  }

  post {
    always {
      sh 'docker compose ps || true'
    }
  }
}
