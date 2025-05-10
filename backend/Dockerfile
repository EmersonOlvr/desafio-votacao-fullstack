FROM maven:3.9.6-eclipse-temurin-21 AS build
RUN mkdir -p /home/app/backend
COPY . /home/app/backend/
WORKDIR /home/app/backend
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /home/app/backend
COPY --from=build /home/app/backend/target/*.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
