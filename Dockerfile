# base-image
FROM openjdk:11

# COPY에서 사용될 경로 변수
ARG JAR_FILE=build/libs/*.jar

# JVM + 시스템 시간대 Asia/Seoul로 설정
ENV TZ=Asia/Seoul

# jar 빌드 파일을 도커 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# jar 파일 실행 server 쓸거면 local -> server로 바꿔야함 + 타임존 추가
# ENTRYPOINT ["java","-Dspring.profiles.active=server","-jar","/app.jar"]
ENTRYPOINT ["java", "-Dspring.profiles.active=server", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]