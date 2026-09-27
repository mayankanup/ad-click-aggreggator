# Backend API + Thymeleaf UI
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY ad-click-common ./ad-click-common
COPY ad-click-producer ./ad-click-producer
COPY ad-click-flink-job ./ad-click-flink-job
COPY ad-click-service ./ad-click-service
RUN mvn -B -q -pl ad-click-service -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/ad-click-service/target/ad-click-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
