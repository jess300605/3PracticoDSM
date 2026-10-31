package com.example.data.repository

import com.example.data.model.LocalizedException
import com.example.R
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

    companion object {
        /** Código que debe ingresar quien se registra como Docente (se documenta en el README). */
        const val TEACHER_ACCESS_CODE = "UDB-DOCENTE-2026"
    }

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
        role: UserRole,
        teacherCode: String = ""
    ): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = nombre.trim()

        // Las cuentas Docente solo se crean con el código de acceso institucional
        if (role == UserRole.DOCENTE && teacherCode.trim() != TEACHER_ACCESS_CODE) {
            return Result.failure(LocalizedException(R.string.err_teacher_code_invalid))
        }

        if (trimmedName.isEmpty()) {
            return Result.failure(LocalizedException(R.string.err_name_empty))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(LocalizedException(R.string.err_email_invalid))
        }

        val validation = PasswordValidator.validate(password)
        if (!validation.isValid) {
            return Result.failure(LocalizedException(R.string.err_password_weak))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(LocalizedException(R.string.err_email_exists))
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
            ?: return Result.failure(LocalizedException(R.string.err_user_not_found))

        val inputHash = hashPassword(password)
        if (userEntity.passwordHash != inputHash) {
            return Result.failure(LocalizedException(R.string.err_wrong_password))
        }

        val user = userEntity.toDomain()
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
