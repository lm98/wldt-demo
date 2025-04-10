# Use an official OpenJDK runtime as a parent image for Java 21
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and project files
COPY . /app

# Build the application using Gradle
RUN ./gradlew build --no-daemon
