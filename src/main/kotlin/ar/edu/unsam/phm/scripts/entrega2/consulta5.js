//Integrante 5: Saber qué libros tienen todos las reservas cumplidas (ya devolvieron los libros)

// el db["libros"] se cambiara cuando se aplique sharding y replicacion

db["libros"].find({
    "fechasReserva": { $ne: [], $exists: true },
    $expr: {
        $allElementsTrue: {
            $map: {
                input: "$fechasReserva",
                as: "reserva",
                in: { $lt: [ "$$reserva.fechaFin", new Date() ] }
            }
        }
    }
})