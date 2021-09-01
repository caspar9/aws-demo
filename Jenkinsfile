pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '7', artifactNumToKeepStr: '10', daysToKeepStr: '5'))
        timeout(time: 12, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        JOB_NAME = 'aws-demo'
    }

    agent any

    stages {
        //start stage
        stage('PrepareEnv') {
            steps {
                script {
                    echo "Prepare environment"
                    sh "echo BRANCH : ${env.BRANCH_NAME}"
                    sh "echo WORKSPACE : ${env.WORKSPACE}"
                    sh "echo GIT_BRANCH : ${env.GIT_BRANCH}"
                    sh "echo BUILD_NUMBER : ${env.BUILD_NUMBER}"
                    sh "echo JOB_NAME : ${env.JOB_NAME}"
                    sh "/usr/local/maven/bin/mvn -v"
                    sh "java -version"
                }
            }
        }

        stage('UnitTest') {
            steps {
                script {
                    echo "run unit test"
                    sh '/usr/local/maven/bin/mvn clean test'
                    //sh "mvn clean test jacoco:report"
                }
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv(installationName: 'sonar') {
                    echo "run sonar scan"
                    sh '/usr/local/maven/bin/mvn sonar:sonar'
                }
            }
        }

        stage('CheckReport') {
            steps {
                echo "check sonar report"
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Jar') {
            steps {
                script {
                    echo 'Build Jar'
                    sh '/usr/local/maven/bin/mvn clean package'
                }
            }
        }

        stage('build docker image') {
            steps {
                script {
                    echo 'docker image build'
                }
            }
        }

       stage('PushECR') {
                steps {
                    script {
                        echo "Push docker image to AWS ECR"
                    }
                }
       }

       stage('Deploy') {
                steps {
                    script {
                        echo 'deploy to dev'
                    }
                }
       }

//end stage
    }
}