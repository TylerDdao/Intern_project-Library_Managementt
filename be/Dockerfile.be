# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy application jar and seed default book cover image
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

COPY --chown=spring:spring uploads/book-covers/*.jpg /app/uploads/book-covers/
COPY --chown=spring:spring uploads/book-covers/*.webp /app/uploads/book-covers/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]