# Etapa 1: usa Maven con JDK 21 para compilar (este proyecto no incluye mvnw).
FROM maven:3.9-eclipse-temurin-21 AS build

# Define la carpeta de trabajo dentro del contenedor de compilación.
WORKDIR /app

# Copia el pom.xml para descargar dependencias antes de copiar todo el código.
COPY pom.xml .

# Descarga dependencias para mejorar la caché de Docker.
RUN mvn -B dependency:go-offline

# Copia el código fuente de la aplicación.
COPY src src

# Compila la aplicación y genera el JAR sin ejecutar tests.
RUN mvn -B clean package -DskipTests

# Etapa 2: usa solo JRE 21 para ejecutar la aplicación, dejando fuera herramientas de compilación.
FROM eclipse-temurin:21-jre

# Define la carpeta de trabajo dentro del contenedor final.
WORKDIR /app

# Copia el JAR generado en la etapa de compilación y lo renombra a app.jar.
COPY --from=build /app/target/*.jar app.jar

# Documenta el puerto interno usado por el API Gateway.
EXPOSE 8080

# Ejecuta la aplicación Spring Boot cuando el contenedor inicia.
ENTRYPOINT ["java", "-jar", "app.jar"]
