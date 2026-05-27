#!/bin/bash

echo "========================================="
echo "Inicializando Config Server Replica Set"
echo "========================================="

docker exec mongo-config-01 mongosh --quiet --file /scripts/init.config.server.js

echo ""
echo "Esperando config servers..."
sleep 15

echo "========================================="
echo "Inicializando Replica Set Shard 1"
echo "========================================="

docker exec shard-01-node-a mongosh --quiet --file /scripts/init.shard01.js

echo ""
echo "========================================="
echo "Inicializando Replica Set Shard 2"
echo "========================================="

docker exec shard-02-node-a mongosh --quiet --file /scripts/init.shard02.js

echo ""
echo "Esperando shards..."
sleep 15

echo "========================================="
echo "Agregando shards al router"
echo "========================================="

docker exec router-01 mongosh --quiet --file /scripts/init.router.js

echo ""
echo "Esperando propagación..."
sleep 15

echo "========================================="
echo "Habilitando sharding para la DB (libros y logs)"
echo "========================================="

docker exec router-01 mongosh --quiet --eval '
sh.enableSharding("booklibre");

db = db.getSiblingDB("booklibre");

db.createCollection("libros");

sh.shardCollection(
  "booklibre.libros",
  { _id: "hashed" }
);

db.createCollection("libro_click_logs");

sh.shardCollection(
  "booklibre.libro_click_logs",
  { libroId: "hashed" }
);
'

echo ""
echo "========================================="
echo "Estado final del cluster"
echo "========================================="

docker exec router-01 mongosh --quiet --eval 'sh.status()'

echo ""
echo "Cluster MongoDB Sharding inicializado correctamente."