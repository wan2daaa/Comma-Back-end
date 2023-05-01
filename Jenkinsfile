pipeline {
    agent any

    stages {
        stage('git Clone') {
            steps {
                git branch: 'develop', url: 'https://github.com/CommaProject/Comma-Back-end'
                echo 'Clone Success'
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean test bootJar
                '''
                echo 'Build and Test Success!'
            }
        }

        stage('Deploy') {
            steps {
                dir('build/libs'){
                    sh '''
                    CURRENT_PID=$(ps -ef | grep java | grep comma | grep -v nohup | awk '{print $2}')
                    if [ -z ${CURRENT_PID} ] ; then
                        echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
                    else
                        echo "> 실행중인 어플리케이션 : $CURRENT_PID"
                        sudo kill -9 $CURRENT_PID
                        sleep 10
                    fi

                    echo "> comma 배포 작업 시작"
                    JENKINS_NODE_COOKIE=dontKillMe nohup java -jar -Dspring.profiles.active={dev} api-0.0.1-SNAPSHOT.jar&
                    '''
                }
            }
        }
    }
}
