nohup java -jar -Dserver.port=8081 ./target/spring-boot-1.0-SNAPSHOT.jar >8081.log 2>&1  &
nohup java -jar -Dserver.port=8082 ./target/spring-boot-1.0-SNAPSHOT.jar >8082.log 2>&1  &
nohup java -jar -Dserver.port=8083 ./target/spring-boot-1.0-SNAPSHOT.jar >8083.log 2>&1  &
nohup java -jar -Dserver.port=8084 ./target/spring-boot-1.0-SNAPSHOT.jar >8084.log 2>&1  &

# ps -ef|grep spring-boot-1.0-SNAPSHOT.jar |awk '{print $2}'|xargs kill -9