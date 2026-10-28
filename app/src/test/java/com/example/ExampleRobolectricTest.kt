package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PasswordValidator
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AprendeApp", appName)
    }

    @Test
    fun `password validator enforces minimum 12 chars and complexity`() {
        // Less than 12 chars
        val shortPwd = PasswordValidator.validate("Pass123!@")
        assertFalse(shortPwd.isValid)
        assertFalse(shortPwd.hasMin12Chars)

        // Missing uppercase
        val noUpper = PasswordValidator.validate("mypassword123!@#$")
        assertFalse(noUpper.isValid)
        assertFalse(noUpper.hasUpperCase)

        // Missing lowercase
        val noLower = PasswordValidator.validate("MYPASSWORD123!@#$")
        assertFalse(noLower.isValid)
        assertFalse(noLower.hasLowerCase)

        // Missing number
        val noNumber = PasswordValidator.validate("MyPasswordSpecial!@#$")
        assertFalse(noNumber.isValid)
        assertFalse(noNumber.hasNumber)

        // Missing special char
        val noSpecial = PasswordValidator.validate("MyPassword1234567")
        assertFalse(noSpecial.isValid)
        assertFalse(noSpecial.hasSpecialChar)

        // Valid 12+ chars with all requirements
        val validPwd = PasswordValidator.validate("Docente2026!@#$")
        assertTrue(validPwd.isValid)
        assertTrue(validPwd.hasMin12Chars)
        assertTrue(validPwd.hasUpperCase)
        assertTrue(validPwd.hasLowerCase)
        assertTrue(validPwd.hasNumber)
        assertTrue(validPwd.hasSpecialChar)
    }

    @Test
    fun `user roles display correct names`() {
        assertEquals("Docente", UserRole.DOCENTE.displayName())
        assertEquals("Estudiante", UserRole.ESTUDIANTE.displayName())
    }
}
