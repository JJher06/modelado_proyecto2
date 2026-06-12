# Dockerfile — MusicDB
# Aplicación JavaFX con soporte X11 para Linux
#
# Build:
#   docker build -t musicdb .
#
# Run (con X11 forwarding):
#   xhost +local:docker
#   docker run -e DISPLAY=$DISPLAY \
#              -v /tmp/.X11-unix:/tmp/.X11-unix \
#              -v $(pwd)/data:/app/data \
#              musicdb

FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copiar archivos del proyecto Maven
COPY pom.xml .
COPY src ./src

# Instalar Maven y compilar
RUN apt-get update && apt-get install -y maven && \
    mvn package -DskipTests --no-transfer-progress

# ---------------------------------------------------------------
# Imagen final — más ligera
# ---------------------------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Dependencias para JavaFX en Linux (X11)
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1 \
    fonts-noto \
    && rm -rf /var/lib/apt/lists/*

# Copiar el JAR construido
COPY --from=builder /app/target/musicdb-1.0.jar ./musicdb.jar

# Directorio para la base de datos (montado como volumen)
RUN mkdir -p /app/data
VOLUME ["/app/data"]

# Variable de entorno para que SQLite guarde la BD en /app/data
ENV DB_PATH=/app/data/musicdb.db

COPY --from=builder /app/target/libs ./libs
CMD ["java", "--module-path", "libs", "--add-modules", "javafx.controls,javafx.fxml,javafx.media", "-jar", "musicdb.jar"]