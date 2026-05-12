package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.RefreshToken


interface RefreshTokenPort {
    fun generateAndSaveRefreshToken(username: String): String
    fun validateRefreshToken(token: String): RefreshToken?
    fun revokeToken(token: String)
    fun revokeByUsername(username: String)
}

