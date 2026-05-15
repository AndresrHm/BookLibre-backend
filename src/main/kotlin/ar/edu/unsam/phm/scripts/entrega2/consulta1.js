//Integrante 1: Saber que libro es el mas clickeado.

db["libro_click_logs"].aggregate([
    {$sortByCount: "$libroId"},
    {$limit: 1},
    {$addFields: {
            libroLogId: {$toObjectId: "$_id"}
        }},
    {$lookup:{
            from: "libros",
            localField: "libroLogId",
            foreignField: "_id",
            as: "libro"}},
    {$unwind: "$libro"}
])