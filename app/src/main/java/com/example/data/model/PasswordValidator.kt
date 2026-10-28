package com.example.data.model

data class PasswordValidationResult(
    val hasMin12Chars: Boolean,
    val hasUpperCase: Boolean,
    val hasLowerCase: Boolean,
    val hasNumber: Boolean,
    val hasSpecialChar: Boolean
) {
    val isValid: Boolean
        get() = hasMin12Chars && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar
}

object PasswordValidator {
    private val UPPERCASE_REGEX = Regex("[A-Z]")
    private val LOWERCASE_REGEX = Regex("[a-z]")
    private val DIGIT_REGEX = Regex("[0-9]")
    private val SPECIAL_CHAR_REGEX = Regex("[!@#$%^&*]")

    fun validate(password: String): PasswordValidationResult {
        return PasswordValidationResult(
            hasMin12Chars = password.length >= 12,
            hasUpperCase = password.contains(UPPERCASE_REGEX),
            hasLowerCase = password.contains(LOWERCASE_REGEX),
            hasNumber = password.contains(DIGIT_REGEX),
            hasSpecialChar = password.contains(SPECIAL_CHAR_REGEX)
        )
    }
}
