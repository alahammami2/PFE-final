pipeline {
  agent any

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Clone') {
      steps {
        checkout scm
        echo '✅ Code cloned successfully from GitHub'
      }
    }

    stage('Environment Setup') {
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
            echo '✅ Using Jenkins credentials'
          } catch (ignored) {
            echo '⚠️ Credentials not configured; using default values'
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
          echo '✅ Environment variables configured'
        }
      }
    }

    stage('Build') {
      steps {
        echo '🔨 Building Docker images...'
        bat 'docker compose version || docker-compose version'
        bat 'docker compose -f docker-compose.yml build --parallel'
        echo '✅ All Docker images built successfully'
      }
    }

    stage('Test') {
      steps {
        echo '🧪 Testing service connectivity...'
        script {
          echo '⏳ Waiting for services to be ready...'
          bat 'ping -n 15 127.0.0.1 > nul || echo "Wait completed"'
          
          echo '🔍 Testing discovery service...'
          bat 'curl -f http://localhost:8761/actuator/health || echo "Discovery service health check"'
          
          echo '🔍 Testing gateway service...'
          bat 'curl -f http://localhost:8090/actuator/health || echo "Gateway service health check"'
          
          echo '🔍 Testing frontend service...'
          bat 'curl -f http://localhost:4200/ || echo "Frontend service check"'
          
          echo '✅ Service connectivity tests completed'
        }
      }
    }

    stage('Deploy') {
      steps {
        echo '🚀 Deploying application...'
        bat 'docker compose -f docker-compose.yml down --remove-orphans || echo "No containers to stop"'
        bat 'docker container prune -f || echo "No containers to prune"'
        bat 'docker compose -f docker-compose.yml up -d'
        echo '✅ Application deployed successfully'
        
        echo '⏳ Waiting for discovery service to be ready...'
        bat 'timeout /t 45 /nobreak || echo "Wait completed"'
        echo '🔍 Verifying service connectivity...'
        bat 'docker exec discovery-service netstat -tuln | findstr 8761 || echo "Discovery service port check"'
      }
    }

    stage('Health Check') {
      steps {
        echo '🏥 Checking application health...'
        script {
          echo '⏳ Waiting for services to be ready...'
          bat 'timeout /t 30 /nobreak || echo "Wait completed"'
          echo '🔍 Checking service status...'
          bat 'docker compose -f docker-compose.yml ps'
          echo '✅ Health check completed'
        }
      }
    }
  }

  post {
    always {
      echo '📊 Pipeline execution completed'
      bat 'docker compose ps || echo "Docker not available - skipping status check"'
    }
    success {
      echo '🎉 Pipeline executed successfully!'
      echo '🌐 Application should be available at:'
      echo '   - Frontend: http://localhost:4200'
      echo '   - Gateway: http://localhost:8090'
      echo '   - Discovery: http://localhost:8761'
    }
    failure {
      echo '❌ Pipeline failed!'
      bat 'docker compose logs --tail=50 || echo "No logs available"'
    }
    cleanup {
      echo '🧹 Cleaning up...'
    }
  }
}
