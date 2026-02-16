# ============================================================================
# Dockerfile Backend Spring Boot
# ============================================================================

# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY src ./src

# Compiler l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Variables d'environnement par défaut (peuvent être overridées par docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]
