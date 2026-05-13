sh.addShard("rs-shard-01/shard-01-node-a:27017")
sh.addShard("rs-shard-02/shard-02-node-a:27017")

// mongo descubre automágicamente los demás miembros del replicaset.
//sh.addShard("rs-shard-01/shard01-b:27017")
//sh.addShard("rs-shard-01/shard01-c:27017")
//sh.addShard("rs-shard-02/shard02-b:27017")
//sh.addShard("rs-shard-02/shard02-c:27017")
