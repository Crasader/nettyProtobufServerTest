#!/bin/bash
SCRIPT=$0
#获取进程名称，必须为完整程序名，否则可能会误操作其他进程
APP_NAME=$1
#获取操作符
OPERATOR=$2
LOG_DIR=/data/logs/edaijia


#jvm参数-堆
JAVA_OPTS="-server -Xms3550m -Xmx3550m -Xmn2g -Xss256k -XX:NewRatio=4  -XX:SurvivorRatio=4 -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
#jvm参数-GC
JAVA_OPTS=" ${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly"
#jvm参数-优化
JAVA_OPTS=" ${JAVA_OPTS} -XX:+AlwaysPreTouch -Djava.awt.headless=true -XX:-OmitStackTraceInFastThrow"
#jvm参数-内存溢出
JAVA_OPTS=" ${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}"
#jvm参数-GC LOG
JAVA_OPTS=" ${JAVA_OPTS} -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:${LOG_DIR}/gc.log"




usage() {
    echo "Usage: .   $SCRIPT [app_name] [start|stop|restart|status]"
    exit 1
}

#判断是否输入了两个参数
#注意①
if [ $# != 2 ]; then
    usage
fi

echo 'log目录 /data/logs/edaijia/driverTraceService'

is_exist(){
  #过滤grep命令本身
    #注意②
  #pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  #使用sh xx.sh命令执行的话，启用下面代码
  pid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v $SCRIPT|awk '{print $2}' `
  if [ -z "${pid}" ]; then
  return 1
  else
    return 0
  fi
}

start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
  else
    cd /data/soft/git/nettyProtobufServerTest/
    git checkout master
    git pull
    mvn clean install -U -Dmaven.test.skip=true
    echo '打包完成'
    nohup java  ${JAVA_OPTS} -Dspring.application.name=driverTraceService -Dspring.profiles.active=test  -jar $APP_NAME > /dev/null 2>&1 &
  fi
}

stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
  else
    echo "${APP_NAME} is not running"
  fi  
}

status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. Pid is ${pid}"
  else
    echo "${APP_NAME} is NOT running."
  fi
}

restart(){
  stop
  start
}

case "$OPERATOR" in
  "start")
    start ;;
  "stop")
    stop ;;
  "status")
    status ;;
  "restart")
    restart ;;
  *)
    usage ;;
esac
