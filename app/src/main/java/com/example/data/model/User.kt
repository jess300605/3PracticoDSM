package com.example.data.model

import androidx.annotation.StringRes
import com.example.R

enum class UserRole(@StringRes val labelRes: Int) {
    ESTUDIANTE(R.string.role_student),
    DOCENTE(R.string.role_teacher);

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
