//Integrante 4: Saber qué libros tienen al menos 3 reservas activas.

db.libros.find({
    "fechasReserva": {$ne: [], $exists: true},
    $expr: {
        $gte: [{ // Compara la cantidad de fechas de reserva con 3. Si es mayor o igual, devuelve true
            $size: { // Cuenta las fechas de reserva filtradas
                $filter: { // Filtra únicamente las reservas con fecha de entrega posterior a hoy
                    input: "$fechasReserva",
                    as: "reserva",
                    cond: {$gt: ["$$reserva.fechaFin", new Date()]}
                }
            }
        }, 3]
    }
})