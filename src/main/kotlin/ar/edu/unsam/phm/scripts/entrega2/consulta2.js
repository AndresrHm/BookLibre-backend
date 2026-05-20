// Saber cuántos libros son del tipo coleccionables


db.libros.countDocuments({ tipoLibro: "COLECCIONABLE" });
