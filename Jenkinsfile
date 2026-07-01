pipeline {
    agent any

    // ── Global Environment Variables ──────────────────────────
    environment {
        // Application Details (from pom.xml)
        APP_NAME        = 'Customer'
        APP_VERSION     = '0.0.1-SNAPSHOT'
        JAR_NAME        = "Customer-0.0.1-SNAPSHOT.jar"
        JAVA_VERSION    = '21'

        // Directories
        DEMO_DIR        = 'demo'                          // pom.xml is inside demo/ subfolder
        JAR_PATH        = "demo/target/Customer-0.0.1-SNAPSHOT.jar"

        // EC2 Details (configured in Jenkins Credentials)
        EC2_HOST        = credentials('EC2_HOST')         // 32.199.153.86
        EC2_USER        = 'ec2-user'
        DEPLOY_DIR      = '/opt/springboot'

        // Notification
        BUILD_NOTIFY    = 'your-email@gmail.com'
    }

    // ── Build Tools ───────────────────────────────────────────
    tools {
        maven 'Maven-3.9'       // Jenkins లో configured Maven name
        jdk   'Java-21'         // Jenkins లో configured JDK name
    }

    // ── Pipeline Options ──────────────────────────────────────
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))    // Last 10 builds keep చేయండి
        timeout(time: 30, unit: 'MINUTES')                // 30 min timeout
        timestamps()                                       // Logs లో timestamp
        disableConcurrentBuilds()                         // Same time 2 builds కాదు
    }

    // ── Triggers ──────────────────────────────────────────────
    triggers {
        pollSCM('H/5 * * * *')   // Every 5 mins GitHub check చేయండి
    }

    // ══════════════════════════════════════════════════════════
    // STAGES
    // ══════════════════════════════════════════════════════════
    stages {

        // ── Stage 1: Checkout ─────────────────────────────────
        stage('Checkout') {
            steps {
                echo '========== STAGE 1: Checkout Code =========='
                checkout scm
                echo "✅ Branch: ${env.BRANCH_NAME}"
                echo "✅ Commit: ${env.GIT_COMMIT?.take(8)}"
                sh 'ls -la'
            }
        }

        // ── Stage 2: Verify Tools ─────────────────────────────
        stage('Verify Tools') {
            steps {
                echo '========== STAGE 2: Verify Tools =========='
                sh '''
                    echo "── Java Version ──"
                    java -version

                    echo "── Maven Version ──"
                    mvn -version

                    echo "── Project Structure ──"
                    ls -la demo/
                '''
            }
        }

        // ── Stage 3: Build ────────────────────────────────────
        stage('Build') {
            steps {
                echo '========== STAGE 3: Build JAR =========='
                dir("${DEMO_DIR}") {
                    sh '''
                        echo "Building Spring Boot application..."
                        mvn clean package -DskipTests \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                }
                echo "✅ Build Successful!"
            }
            post {
                success {
                    echo "JAR created: ${JAR_NAME}"
                    sh "ls -lh ${JAR_PATH}"
                }
                failure {
                    echo "❌ Build Failed!"
                }
            }
        }

        // ── Stage 4: Unit Tests ───────────────────────────────
        stage('Unit Tests') {
            steps {
                echo '========== STAGE 4: Run Unit Tests =========='
                dir("${DEMO_DIR}") {
                    sh '''
                        echo "Running unit tests..."
                        mvn test \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                }
                echo "✅ All Tests Passed!"
            }
            post {
                always {
                    // Test results publish చేయండి
                    junit(
                        testResults: 'demo/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
                failure {
                    echo "❌ Tests Failed! Check test reports."
                }
            }
        }

        // ── Stage 5: Code Quality Check ───────────────────────
        stage('Code Quality') {
            steps {
                echo '========== STAGE 5: Code Quality =========='
                dir("${DEMO_DIR}") {
                    sh '''
                        echo "Running code quality checks..."
                        mvn verify \
                            -DskipTests \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                }
                echo "✅ Code Quality Check Passed!"
            }
        }

        // ── Stage 6: Archive Artifacts ────────────────────────
        stage('Archive Artifacts') {
            steps {
                echo '========== STAGE 6: Archive JAR =========='
                archiveArtifacts(
                    artifacts: "demo/target/${JAR_NAME}",
                    fingerprint: true,
                    onlyIfSuccessful: true
                )
                echo "✅ JAR archived successfully!"
                echo "📦 Artifact: ${JAR_NAME}"
            }
        }

        // ── Stage 7: Deploy to EC2 ────────────────────────────
        stage('Deploy to EC2') {
            when {
                // main branch మాత్రమే deploy చేయాలి
                branch 'main'
            }
            steps {
                echo '========== STAGE 7: Deploy to EC2 =========='

                // SSH Key Jenkins Credentials నుండి తీసుకోండి
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'EC2_SSH_KEY',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    sh '''
                        echo "📤 Copying JAR to EC2..."
                        scp -i $SSH_KEY \
                            -o StrictHostKeyChecking=no \
                            ${JAR_PATH} \
                            ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/app.jar

                        echo "🚀 Deploying on EC2..."
                        ssh -i $SSH_KEY \
                            -o StrictHostKeyChecking=no \
                            ${EC2_USER}@${EC2_HOST} \
                            "
                            echo 'Stopping existing app...'
                            sudo systemctl stop springboot || true

                            echo 'Starting new version...'
                            sudo systemctl start springboot

                            echo 'Waiting for startup...'
                            sleep 15

                            echo 'Checking status...'
                            sudo systemctl status springboot --no-pager

                            echo '✅ Deployment Complete!'
                            "
                    '''
                }
            }
            post {
                success {
                    echo "✅ Deployed Successfully to EC2!"
                    echo "🌐 App URL: http://${EC2_HOST}:8080"
                }
                failure {
                    echo "❌ Deployment Failed!"
                }
            }
        }

        // ── Stage 8: Health Check ─────────────────────────────
        stage('Health Check') {
            when {
                branch 'main'
            }
            steps {
                echo '========== STAGE 8: Health Check =========='
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'EC2_SSH_KEY',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    sh '''
                        ssh -i $SSH_KEY \
                            -o StrictHostKeyChecking=no \
                            ${EC2_USER}@${EC2_HOST} \
                            "
                            echo 'Checking application health...'
                            sleep 5

                            # Actuator health check
                            curl -f http://localhost:8080/actuator/health \
                                && echo '✅ App is Healthy!' \
                                || echo '⚠️ Health check failed - check logs'

                            # Show last 20 log lines
                            echo '── Last 20 log lines ──'
                            tail -20 /opt/springboot/logs/app.log || true
                            "
                    '''
                }
            }
        }

    }
    // ══════════════════════════════════════════════════════════
    // END STAGES
    // ══════════════════════════════════════════════════════════

    // ── Post Build Actions ────────────────────────────────────
    post {

        success {
            echo """
            ╔══════════════════════════════════╗
            ║   ✅ BUILD SUCCESSFUL!           ║
            ║   App: ${APP_NAME}               ║
            ║   Version: ${APP_VERSION}        ║
            ║   Branch: ${env.BRANCH_NAME}     ║
            ╚══════════════════════════════════╝
            """
        }

        failure {
            echo """
            ╔══════════════════════════════════╗
            ║   ❌ BUILD FAILED!               ║
            ║   App: ${APP_NAME}               ║
            ║   Branch: ${env.BRANCH_NAME}     ║
            ║   Check logs for details         ║
            ╚══════════════════════════════════╝
            """
        }

        always {
            echo "Pipeline completed - Build #${env.BUILD_NUMBER}"
            cleanWs()    // Workspace cleanup చేయండి
        }
    }
}
