package utils

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "heroes")
@Serializable
data class HeroEntity(
    @PrimaryKey
    val url: String,
    val name: String = "Unknown",
    val culture: String = "Unknown",
    val born: String = "Unknown",
    val titles: String? = null, // будем хранить как строку через запятую
    val aliases: String? = null,
    val playedBy: String? = null,
    val isPlaceholder: Boolean = false
) {
    companion object {
        fun fromModel(model: HeroModel): HeroEntity {
            return HeroEntity(
                url = model.url,
                name = model.name,
                culture = model.culture,
                born = model.born,
                titles = model.titles?.joinToString(","),
                aliases = model.aliases?.joinToString(","),
                playedBy = model.playedBy?.joinToString(","),
                isPlaceholder = model.isPlaceholder
            )
        }

        fun toModel(entity: HeroEntity): HeroModel {
            return HeroModel(
                url = entity.url,
                name = entity.name,
                culture = entity.culture,
                born = entity.born,
                titles = entity.titles?.split(",")?.map { it.trim() },
                aliases = entity.aliases?.split(",")?.map { it.trim() },
                playedBy = entity.playedBy?.split(",")?.map { it.trim() },
                isPlaceholder = entity.isPlaceholder
            )
        }
    }
}