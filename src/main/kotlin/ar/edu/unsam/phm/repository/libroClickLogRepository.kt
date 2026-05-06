package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroClickLog
import org.springframework.data.mongodb.repository.MongoRepository

interface LibroClickLogRepository : MongoRepository<LibroClickLog, String>