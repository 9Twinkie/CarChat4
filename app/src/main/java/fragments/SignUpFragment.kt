package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.carchat.databinding.FragmentSignUpBinding
import utils.UserCredentials

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.registerButton.setOnClickListener {
            val enteredEmail = binding.email.text.toString().trim()
            val enteredUsername = binding.username.text.toString().trim()
            val enteredPassword = binding.password.text.toString().trim()

            if (enteredEmail.isEmpty() || enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(activity, "Enter email, username, and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userCredentials = UserCredentials(
                username = enteredUsername,
                email = enteredEmail,
                password = enteredPassword
            )

            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(userCredentials)
            findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}