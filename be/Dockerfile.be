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

# su-exec lets us start as root (to fix volume ownership) then drop to
# the non-root spring user before running the actual application
RUN apk add --no-cache su-exec

# Copy application jar and seed default book cover image
# (still owned by root at this point; entrypoint fixes ownership at runtime)
COPY --from=build /app/target/*.jar app.jar
COPY uploads/book-covers/*.jpg /app/uploads/book-covers/
COPY uploads/book-covers/*.webp /app/uploads/book-covers/

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]