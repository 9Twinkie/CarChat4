package com.example.carchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carchat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatList = listOf(
            ChatItem("Алексей", "Привет! Купил сегодня японский турбонадув. Поставим сегодня?", "10:15", R.drawable.avatabmw),
            ChatItem("Михаил", "У весны сегодня в 23:00", "09:45", R.drawable.avatardev),
            ChatItem("Иван", "Поехали сегодня семерку готовить?", "08:30", R.drawable.avatarsemerka)
        )

        val adapter = ChatAdapter(chatList)
        binding.chatList.layoutManager = LinearLayoutManager(requireContext())
        binding.chatList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}