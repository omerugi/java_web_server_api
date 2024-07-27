# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copy the POM file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the application port
EXPOSE 8080