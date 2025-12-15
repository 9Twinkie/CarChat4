package utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carchat.R

class HeroCardAdapter(
    private var heroes: MutableList<HeroModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HERO = 1
    private val VIEW_TYPE_NO_MORE_CARDS = 2

    override fun getItemViewType(position: Int): Int {
        return if (heroes[position].isPlaceholder) VIEW_TYPE_NO_MORE_CARDS else VIEW_TYPE_HERO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HERO) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.hero_card_text_only, parent, false)
            HeroViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.no_more_card, parent, false)
            NoMoreCardsViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeroViewHolder) {
            val hero = heroes[position]
            holder.name.text = hero.name.ifEmpty { "Unknown" }

            val details = buildString {
                append("Culture: ${hero.culture.ifEmpty { "—"} }\n")
                append("Born: ${hero.born.ifEmpty { "—"} }\n")

                val titleText = hero.titles?.joinToString(", ") ?: "—"
                append("Title: $titleText\n")

                val aliasText = hero.aliases?.joinToString(", ") ?: "—"
                append("Alias: $aliasText\n")

                val playedByText = hero.playedBy?.joinToString(", ") ?: "—"
                append("Played by: $playedByText")
            }
            holder.details.text = details
        }
    }


    override fun getItemCount(): Int = heroes.size

    fun removeHeroAt(position: Int) {
        if (position < heroes.size) {
            heroes.removeAt(position)
            notifyItemRemoved(position)
            ensurePlaceholder()
        }
    }

    fun ensurePlaceholder() {
        if (heroes.isEmpty() || !heroes.last().isPlaceholder) {
            heroes.add(HeroModel("", isPlaceholder = true))
            notifyItemInserted(heroes.size - 1)
        }
    }

    fun getHeroAt(position: Int): HeroModel = heroes[position]

    class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.hero_name)
        val details: TextView = view.findViewById(R.id.hero_details)
    }



    class NoMoreCardsViewHolder(view: View) : RecyclerView.ViewHolder(view)
}