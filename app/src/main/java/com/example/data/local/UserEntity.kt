package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.User
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val passwordHash: String,
    val role: String // "ESTUDIANTE" or "DOCENTE"
) {
    fun toDomain(): User = User(
        id = id,
        nombre = nombre,
        email = email,
        passwordHash = passwordHash,
        role = try { UserRole.valueOf(role) } catch (_: Exception) { UserRole.ESTUDIANTE }
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            nombre = user.nombre,
            email = user.email.lowercase().trim(),
            passwordHash = user.passwordHash,
            role = user.role.name
        )
    }
}
