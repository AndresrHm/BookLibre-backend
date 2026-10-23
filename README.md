# BookLibre - Backend

API REST desarrollada con **Kotlin** y **Spring Boot** para la aplicación BookLibre.

## Descripción
BookLibre permite a los usuarios pedir, prestar y reclamar libros. Gestiona usuarios, libros, reservas, calificaciones, bibliokarmas y seguimiento de actividad.

## Tecnologías
- Kotlin + Spring Boot
- PostgreSQL (persistencia relacional)
- MongoDB (colección de libros y log de clicks)
- Redis (caché de libros populares y ranking)
- GraphQL (API para métricas/KPI)
- JWT (autenticación y autorización)
- Docker

## Funcionalidades principales
- **Autenticación**: Login/Registro con JWT, roles (lector, publicador, lector/publicador)
- **Libros**: CRUD, búsqueda con filtros y paginado, disponibilidad por fechas
- **Reservas**: Solicitud, seguimiento de préstamos prestados/recibidos, calificación
- **Usuarios**: Perfil, libros ofrecidos, bibliokarmas
- **Persistencia**: PostgreSQL + MongoDB + Redis
- **GraphQL**: Métricas (conversión, leaderboard, estado del catálogo, calificaciones, feed de actividad)

## Instalación
```bash
# Con Docker
docker-compose up
```

Ver configuración en `application.yml` para conexión a PostgreSQL, MongoDB y Redis.
