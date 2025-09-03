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

    stage('Prepare .env') {
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
            echo 'Credentials not configured; using default values'
            groq = 'sk_1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef'
            jwt = 'my_super_secret_jwt_key_for_volleyball_platform_2024'
            dbp = 'volleyball_db_password_2024'
            mailUser = 'your_email@gmail.com'
            mailPass = 'your_app_password_here'
          }

          writeFile file: '.env', text: """GROQ_API_KEY=${groq}
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
        bat 'docker compose version || docker-compose version'
        bat 'docker compose -f docker-compose.yml up -d --build'
      }
    }
  }

  post {
    always {
      bat 'docker compose ps || echo "Docker compose ps failed"'
    }
  }
}
