FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY build/libs/todoapp-0.0.1-SNAPSHOT.jar /app/todoapp.jar

CMD ["java", "-jar", "/app/todoapp.jar"]
