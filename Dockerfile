# ======================
# Étape 1 : Build
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Meilleur cache Docker: dépendances d'abord
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Puis les sources
COPY src ./src

# Build du projet (sans tests pour aller plus vite sur Render)
RUN mvn -DskipTests clean package

# ======================
# Étape 2 : Runtime
# ======================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copier le JAR généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Port exposé (indicatif). Render fournit réellement le port via $PORT.
EXPOSE 8080

# Lancer l'application (Spring écoute sur le port Render)
CMD ["sh", "-c", "java -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
