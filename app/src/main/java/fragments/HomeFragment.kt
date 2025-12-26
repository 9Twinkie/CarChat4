package fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carchat.databinding.ButtonsNavBinding
import com.example.carchat.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.HeroCardAdapter
import utils.HeroModel
import utils.api.RetrofitInstance
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {

    // В начале HomeFragment
    private var currentStartId = 1 // начнём с героя №1
    private val loadCount = 50

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        val likedHeroes = mutableListOf<HeroModel>()
        val dislikedHeroes = mutableListOf<HeroModel>()
    }

    private var heroCardAdapter: HeroCardAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        setupNavigation()
        setupRecyclerView()
        observeHeroesFromDb()
        coldStartLoad()
        setupSwipeGesture()
        return view
    }

    private fun setupNavigation() {
        val navBinding = ButtonsNavBinding.bind(binding.root)
        navBinding.buttonFunction1.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingFragments())
        }
        navBinding.buttonFunction3.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRatedHeroesButtonsFragment())
        }
        navBinding.buttonRefresh.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val repo = RetrofitInstance.getRepository(requireContext())
                    repo.loadHeroesByIdRange(currentStartId, loadCount)
                    currentStartId += loadCount
                    Toast.makeText(requireContext(), "Загружено героев с ${currentStartId - loadCount} по ${currentStartId - 1}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        heroCardAdapter = HeroCardAdapter(mutableListOf())
        binding.recyclerView.apply {
            adapter = heroCardAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeHeroesFromDb() {
        lifecycleScope.launch {
            val repo = RetrofitInstance.getRepository(requireContext())
            repo.getHeroesFromDb().collectLatest { heroes ->
                Log.d("HomeFragment", "Получено ${heroes.size} героев из Room")
                heroCardAdapter?.heroes = heroes.toMutableList()
                heroCardAdapter?.notifyDataSetChanged()
                heroCardAdapter?.ensurePlaceholder()
                if (heroes.isNotEmpty()) {
                    saveHeroesToFile(requireContext(), heroes)
                }
            }
        }
    }

    private fun coldStartLoad() {
        lifecycleScope.launch {
            val repo = RetrofitInstance.getRepository(requireContext())
            if (!repo.hasDataInDb()) {
                try {
                    // Загружаем первые 50 героев (как раньше)
                    repo.fetchAndSaveFromApi()
                    currentStartId = 51 // следующие начнутся с 51
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Ошибка холодного старта", e)
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun saveHeroesToFile(context: Context, heroes: List<HeroModel>): File? {
        return withContext(Dispatchers.IO) {
            val fileName = "21.txt"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            try {
                FileOutputStream(file).use { fos ->
                    heroes.forEach { hero ->
                        val info = """
Name: ${hero.name}
Culture: ${hero.culture}
Born: ${hero.born}
Titles: ${hero.titles?.joinToString(", ") ?: "—"}
Aliases: ${hero.aliases?.joinToString(", ") ?: "—"}
Played By: ${hero.playedBy?.joinToString(", ") ?: "—"}
""".trimIndent()
                        fos.write(info.toByteArray())
                        fos.write("\n".toByteArray())
                    }
                }
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun setupSwipeGesture() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return

                val hero = heroCardAdapter?.getHeroAt(pos) ?: return
                if (hero.isPlaceholder) {
                    heroCardAdapter?.notifyItemChanged(pos)
                    return
                }

                if (direction == ItemTouchHelper.RIGHT) {
                    likedHeroes.add(hero)
                    Toast.makeText(requireContext(), "Liked ${hero.name}", Toast.LENGTH_SHORT).show()
                } else {
                    dislikedHeroes.add(hero)
                    Toast.makeText(requireContext(), "Disliked ${hero.name}", Toast.LENGTH_SHORT).show()
                }

                heroCardAdapter?.removeHeroAt(pos)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}