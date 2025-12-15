package utils

import java.io.Serializable

data class UserCredentials(
    val username: String,
    val email: String,
    val password: String
) : Serializable

