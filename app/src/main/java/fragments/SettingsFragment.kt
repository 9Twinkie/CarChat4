package fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.carchat.R
import com.example.carchat.databinding.ButtonsNavBinding
import com.example.carchat.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.ThemePreferences
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val LANGUAGE_PREFERENCE = "language_pref"
    private val SELECTED_LANGUAGE = "selected_language"

    private lateinit var themePreferences: ThemePreferences
    private var themeSwitchJob: Job? = null
    private val FILE_NAME = "21.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(LANGUAGE_PREFERENCE, 0)
        themePreferences = ThemePreferences(requireContext())

        updateFileStatus()

        binding.deleteFileButton.setOnClickListener { deleteFile() }
        binding.restoreFileButton.setOnClickListener { restoreFileFromInternalStorage() }

        setupThemeSwitch()
        setupLanguageSpinner()
        setupNavigation()

        return binding.root
    }

    private fun updateFileStatus() {
        val externalFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILE_NAME)
        val internalFile = File(requireContext().filesDir, FILE_NAME)

        if (externalFile.exists()) {
            binding.fileStatusTextView.setText(R.string.file_exists)
            binding.deleteFileButton.visibility = View.VISIBLE
            binding.restoreFileButton.visibility = View.GONE
        } else {
            binding.fileStatusTextView.setText(R.string.file_does_not_exist)
            binding.deleteFileButton.visibility = View.GONE
            if (internalFile.exists()) {
                binding.backupFileStatusTextView.setText(R.string.backup_file_exists)
                binding.restoreFileButton.visibility = View.VISIBLE
            } else {
                binding.backupFileStatusTextView.setText(R.string.backup_file_does_not_exist)
                binding.restoreFileButton.visibility = View.GONE
            }
        }
    }

    private fun deleteFile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val externalFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILE_NAME)
            val internalFile = File(requireContext().filesDir, FILE_NAME)

            if (!externalFile.exists()) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Файл не найден", Toast.LENGTH_SHORT).show()
                        updateFileStatus()
                    }
                }
                return@launch
            }

            try {
                FileInputStream(externalFile).use { input ->
                    FileOutputStream(internalFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val deleted = externalFile.delete()

                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    if (deleted) {
                        Toast.makeText(requireContext(), "Файл удалён", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Не удалось удалить файл", Toast.LENGTH_SHORT).show()
                    }
                    updateFileStatus()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Ошибка резервного копирования", Toast.LENGTH_SHORT).show()
                        updateFileStatus()
                    }
                }
            }
        }
    }

    private fun restoreFileFromInternalStorage() {
        lifecycleScope.launch(Dispatchers.IO) {
            val internalFile = File(requireContext().filesDir, FILE_NAME)
            val externalFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FILE_NAME)

            if (!internalFile.exists()) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Резервная копия отсутствует", Toast.LENGTH_SHORT).show()
                        updateFileStatus()
                    }
                }
                return@launch
            }

            try {
                FileInputStream(internalFile).use { input ->
                    FileOutputStream(externalFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val backupDeleted = internalFile.delete()

                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    if (backupDeleted) {
                        Toast.makeText(requireContext(), "Файл восстановлен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Не удалось удалить резервную копию", Toast.LENGTH_SHORT).show()
                    }
                    updateFileStatus()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Ошибка восстановления", Toast.LENGTH_SHORT).show()
                        updateFileStatus()
                    }
                }
            }
        }
    }

    private fun setupThemeSwitch() {
        themeSwitchJob = lifecycleScope.launch {
            themePreferences.isDarkMode.collect { isDarkMode ->
                if (isAdded) {
                    binding.themeSwitch.isChecked = isDarkMode
                    AppCompatDelegate.setDefaultNightMode(
                        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                if (isAdded) {
                    themePreferences.setDarkMode(isChecked)
                }
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("en", "ru")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf("English", "Русский")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        val selectedLanguage = sharedPreferences.getString(SELECTED_LANGUAGE, "en")
        binding.languageSpinner.setSelection(languages.indexOf(selectedLanguage))

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val newLanguage = languages[position]
                    if (newLanguage != selectedLanguage) {
                        val editor = sharedPreferences.edit()
                        editor.putString(SELECTED_LANGUAGE, newLanguage)
                        editor.apply()
                        Log.d("SettingsFragment", "Language changed to $newLanguage")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun setupNavigation() {
        val navBinding = ButtonsNavBinding.bind(binding.root)
        navBinding.buttonFunction2.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(SettingsFragmentDirections.actionFirstFunctionFragmentToHomeFragment())
            }
        }
        navBinding.buttonFunction3.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(SettingsFragmentDirections.actionFirstFunctionFragmentToRatedHeroesButtonsFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        themeSwitchJob?.cancel()
        _binding = null
    }
}