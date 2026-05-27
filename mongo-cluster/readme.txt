// ----- Forma automágica ----- //
0. docker compose up -d
1. ./init-cluster.sh (hay que darle permisos de ejecución)

// ----- Forma manual ----- //
--- Levantar cluster
Pasos a seguir para ejecutar los scripts
El orden es entrar al contenedor y luego ejecutar el script

0. docker compose up -d

1. Inicializar config server replica set
    docker exec -it mongo-config-01 mongosh

    load("/scripts/init.config.server.js")

2. Inicializar replica set shard 1
    docker exec -it shard-01-node-a mongosh

    load("/scripts/init.shard01.js")

3. Inicializar replica set del shard 2
    docker exec -it shard-02-node-a mongosh

    load("/scripts/init.shard02.js")

4. Agregar los shards al router
    docker exec -it router-01 mongosh

    load("/scripts/init.router.js")

5. Verificar que el router quedó bien
    sh.status()

    deberíamos ver:
        rs-shard-01
        rs-shard-02
        config servers
        chunks
        balancer

Esta configuración hay que hacerla una vez y listo,
en caso de tirar los volúmenes se deberá hacer de nuevo.

--- Shard Key
6. Entrar al router (mongos) y habilitar sharding para la db booklibre
docker exec -it router-01 mongosh

sh.enableSharding("booklibre")

sh.status() -> verificamos que aparezca booklibre

7. Creamos la colección libros
use booklibre

db.createCollection("libros")

show collections -> verificamos

8. Creamos la shard key (ejemplo usando id y hash function)
sh.shardCollection(
  "booklibre.libros",
  { _id: "hashed" }
)

9. Creamos la colección de logs de clicks
db.createCollection("libro_click_logs")

show collections -> verificamos

10. Creamos la shard key para los logs usando libroId

sh.shardCollection(
  "booklibre.libro_click_logs",
  { libroId: "hashed" }
)

sh.status() -> verificamos que quedó hasheada