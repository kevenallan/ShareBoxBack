FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

# Define a variável de ambiente para o profile de produção
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

COPY --from=build /target/sharebox-5.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]