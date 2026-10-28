package com.example.data.model

enum class UserRole {
    ESTUDIANTE,
    DOCENTE;

    fun displayName(): String = when (this) {
        ESTUDIANTE -> "Estudiante"
        DOCENTE -> "Docente"
    }
}

data class User(
    val id: String,
    val nombre: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole
)
