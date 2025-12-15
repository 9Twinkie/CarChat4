package utils

import kotlinx.serialization.Serializable

@Serializable
data class HeroModel(
    val url: String,
    val name: String = "Unknown",
    val culture: String = "Unknown",
    val born: String = "Unknown",
    val titles: List<String>? = null, // ← Может быть null!
    val aliases: List<String>? = null, // ← Может быть null!
    val playedBy: List<String>? = null, // ← Может быть null!
    val isPlaceholder: Boolean = false
) {
    val id: Int
        get() = url.substringAfterLast('/').toIntOrNull() ?: 0
}