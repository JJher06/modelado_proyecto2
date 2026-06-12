# MusicDB

Base de datos musical con interfaz gráfica JavaFX.  
Proyecto 2 — Bases de Datos, UNAM.

## Licencia

Este proyecto se distribuye bajo la licencia **GNU GPL v3**.  
Ver [LICENSE](LICENSE) para más detalles.

## Requisitos

- Linux (Ubuntu 20.04+)
- JDK 21
- Maven 3.9+
- Docker (obligatorio para entrega)

## Compilar y ejecutar

```bash
# Sin Docker
mvn javafx:run

# Con Docker (recomendado para entrega)
xhost +local:docker
docker compose up
```

## Estructura del proyecto

```
musicdb/
├── src/
│   ├── main/
│   │   ├── java/mx/unam/musicdb/
│   │   │   ├── App.java              # Punto de entrada
│   │   │   ├── model/                # Entidades (Cancion, Album...)
│   │   │   ├── dao/                  # Patrón DAO — acceso a SQLite
│   │   │   ├── controller/           # Controladores MVC
│   │   │   ├── miner/                # Minero de archivos MP3
│   │   │   └── util/                 # DatabaseConnection, helpers
│   │   └── resources/
│   │       ├── fxml/                 # Vistas JavaFX
│   │       ├── css/                  # Estilos
│   │       └── schema.sql            # Esquema de la BD
│   └── test/                         # Pruebas unitarias (JUnit 5)
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Patrones de diseño

- **MVC** — Model/View/Controller para la interfaz gráfica
- **DAO** — Data Access Object para el acceso a SQLite

## Estado del proyecto

- [x] Estructura base Maven + JavaFX
- [x] Capa DAO provisional
- [x] Minero de etiquetas ID3
- [x] Vista principal (tabla de canciones)
- [x] Docker con X11
- [ ] Esquema oficial de BD (pendiente del profesor)
- [ ] Pruebas unitarias completas
- [ ] Compilador de búsquedas
- [ ] Reproducción de MP3 (punto extra)
```
