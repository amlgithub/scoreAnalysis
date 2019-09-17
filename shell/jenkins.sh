#!/bin/bash

PROJECT_NAME=score_analysis

pid=`ps -ef |grep ${PROJECT_NAME} |grep -v "grep" |awk '{print $2}' `

if [${pid}]; then

    echo "${PROJECT_NAME} is  running  and pid=$pid"

    kill -9 ${pid}

    if [[ $? -eq 0 ]];then

       echo "success to stop ${PROJECT_NAME} "

    else

       echo "fail to stop ${PROJECT_NAME} "

     fi

fi

old_pid=`ps -ef |grep ${PROJECT_NAME} |grep -v "grep" |awk '{print $2}' `

if [$old_pid]; then

    echo "$PROJECT_NAME  is  running  and pid=$old_pid"

else

   echo "Ready to start ${PROJECT_NAME} ...."

   cd /var/lib/jenkins/workspace/学生发展平台

   echo "切换到jar包路径---> 成功..."

   BUILD_ID=dontKillMe

   nohup java -jar target/sdc-0.0.1-SNAPSHOT.jar  >> catalina.out  2>&1 &

fi

new_pid=`ps -ef |grep ${PROJECT_NAME} |grep -v "grep" |awk '{print $2}' `

if [${new_pid}]; then

    echo "${PROJECT_NAME} start  success  and pid=${new_pid}"

else

	echo "${PROJECT_NAME} start  failed "

fi