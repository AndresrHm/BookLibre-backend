// Integrante 3: Saber qué libros tienen más de 4 puntos de calificación.

db.libros.find({ calificacion: { $gt: 4 } })