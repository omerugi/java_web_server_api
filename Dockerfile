FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080