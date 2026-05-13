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