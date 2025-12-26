package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.carchat.databinding.FragmentRatedHeroesButtonsBinding
import com.example.carchat.databinding.ButtonsNavBinding

class RatedHeroesButtonsFragment : Fragment() {

    private var _binding: FragmentRatedHeroesButtonsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatedHeroesButtonsBinding.inflate(inflater, container, false)
        val view = binding.root
        setupNavigation()
        binding.likedButton.setOnClickListener { navigateToRatedHeroesFragment("liked") }
        binding.dislikedButton.setOnClickListener { navigateToRatedHeroesFragment("disliked") }
        return view
    }

    private fun setupNavigation() {
        val navBinding = ButtonsNavBinding.bind(binding.root)
        navBinding.buttonFunction1.setOnClickListener {
            findNavController().navigate(RatedHeroesButtonsFragmentDirections.actionRatedHeroesButtonsFragmentToSettingsFragment())
        }
        navBinding.buttonRefresh.setOnClickListener {
            findNavController().navigate(RatedHeroesButtonsFragmentDirections.actionRatedHeroesButtonsFragmentToHomeFragment())
        }
    }

    private fun navigateToRatedHeroesFragment(type: String) {
        val action = RatedHeroesButtonsFragmentDirections
            .actionRatedHeroesButtonsFragmentToRatedHeroesFragment(type)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}