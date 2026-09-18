pipeline {
    agent any
    
    environment {
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')
        GIT_CREDENTIALS = credentials('git-credentials')
        MAVEN_HOME = tool('Maven-3.9')
        JAVA_HOME = tool('JDK-17')
    }
    
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/LankeVenkatesh-Java-Developer/Ecommerce-Backend.git',
                    branch: 'main',
                    credentialsId: "${GIT_CREDENTIALS}"
            }
        }
        
        stage('Clean and Build') {
            steps {
                sh """
                    ${MAVEN_HOME}/bin/mvn clean install -DskipTests
                """
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh """
                    ${MAVEN_HOME}/bin/mvn test
                """
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    def services = [
                        'user-management-service',
                        'Products-management-service',
                        'Order-management-service',
                        'Admin-management-service',
                        'Cart-management-service',
                        'notification-management-service',
                        'api-gateway-service',
                        'discovery-service'
                    ]
                    
                    services.each { service ->
                        dir(service) {
                            sh """
                                docker build -t lankevenkatesh/${service}:${BUILD_NUMBER} .
                                docker tag lankevenkatesh/${service}:${BUILD_NUMBER} lankevenkatesh/${service}:latest
                            """
                        }
                    }
                }
            }
        }
        
        stage('Push Docker Images') {
            steps {
                script {
                    sh """
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                    """
                    
                    def services = [
                        'user-management-service',
                        'Products-management-service',
                        'Order-management-service',
                        'Admin-management-service',
                        'Cart-management-service',
                        'notification-management-service',
                        'api-gateway-service',
                        'discovery-service'
                    ]
                    
                    services.each { service ->
                        sh """
                            docker push lankevenkatesh/${service}:${BUILD_NUMBER}
                            docker push lankevenkatesh/${service}:latest
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                sh """
                    docker-compose -f docker-compose.yml down
                    docker-compose -f docker-compose.yml up -d
                """
            }
        }
        
        stage('Health Check') {
            steps {
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    sh """
                        curl -f http://localhost:8087/actuator/health || exit 1
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
        always {
            sh 'docker logout || true'
            cleanWs()
        }
    }
}
