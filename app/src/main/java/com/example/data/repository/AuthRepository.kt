package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.model.PasswordValidator
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun initializeDefaultUsers() {
        // Pre-seed demo users if they don't exist
        val studentEmail = "estudiante@udb.edu.sv"
        val teacherEmail = "docente@udb.edu.sv"

        if (userDao.getUserByEmail(studentEmail) == null) {
            userDao.insertUser(
                UserEntity(
                    id = "demo_student",
                    nombre = "Carlos Mendoza (Estudiante)",
                    email = studentEmail,
                    passwordHash = hashPassword("Estudiante2026!@#$"),
                    role = UserRole.ESTUDIANTE.name
                )
            )
        }

        if (userDao.getUserByEmail(teacherEmail) == null) {
            userDao.insertUser(
                UserEntity(
                    id = "demo_teacher",
                    nombre = "Ing. Roberto Guardado (Docente)",
                    email = teacherEmail,
                    passwordHash = hashPassword("Docente2026!@#$"),
                    role = UserRole.DOCENTE.name
                )
            )
        }
    }

    suspend fun register(
        nombre: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = nombre.trim()

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío."))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("El formato del correo es inválido."))
        }

        val validation = PasswordValidator.validate(password)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException("La contraseña no cumple con todos los requisitos de seguridad."))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta registrada con este correo."))
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            nombre = trimmedName,
            email = trimmedEmail,
            passwordHash = hashPassword(password),
            role = role
        )

        userDao.insertUser(UserEntity.fromDomain(newUser))
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val userEntity = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Usuario no encontrado con ese correo."))

        val inputHash = hashPassword(password)
        if (userEntity.passwordHash != inputHash) {
            return Result.failure(IllegalArgumentException("Contraseña incorrecta."))
        }

        val user = userEntity.toDomain()
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun loginDemo(role: UserRole): User {
        val email = if (role == UserRole.DOCENTE) "docente@udb.edu.sv" else "estudiante@udb.edu.sv"
        val user = userDao.getUserByEmail(email)?.toDomain() ?: User(
            id = if (role == UserRole.DOCENTE) "demo_teacher" else "demo_student",
            nombre = if (role == UserRole.DOCENTE) "Ing. Roberto Guardado (Docente)" else "Carlos Mendoza (Estudiante)",
            email = email,
            passwordHash = "",
            role = role
        )
        _currentUser.value = user
        return user
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
