MusicDB
=======

Base de datos musical con interfaz grafica JavaFX.
Proyecto 2 — My P.


Requisitos
----------

- Linux
- JDK 21
- Maven 3.9+
- Docker


Ejecucion con Docker
-----------------------------------

     xhost +local:docker
     docker compose up --build

Ejecucion local (sin Docker)
-----------------------------

     mvn compile
     mvn javafx:run

Nota: La base de datos se guarda en ./musicdb.db.


Pruebas unitarias
---------------
  mvn test