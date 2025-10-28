package com.example.carchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carchat.databinding.ItemChatBinding

data class ChatItem(
    val name: String,
    val lastMessage: String,
    val time: String,
    val avatar: Int
)

class ChatAdapter(private val chats: List<ChatItem>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatItem) {
            binding.apply {
                avatar.setImageResource(chat.avatar)
                name.text = chat.name
                message.text = chat.lastMessage
                time.text = chat.time
            }
        }
    }
}