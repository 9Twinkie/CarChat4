package utils

import kotlinx.serialization.Serializable

@Serializable
data class HeroModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val title: String,
    val family: String,
    val image: String,
    val imageUrl: String,
    val isPlaceholder: Boolean = false
)

