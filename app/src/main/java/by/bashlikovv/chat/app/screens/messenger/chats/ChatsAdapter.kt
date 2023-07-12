package by.bashlikovv.chat.app.screens.messenger.chats

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.databinding.ChatsListItemBinding

typealias onChatActionListener = (Chat) -> Unit

class ChatsAdapter(
    private val chats: List<Chat>,
    private val onOpenClickListener: onChatActionListener
) : BaseAdapter(), View.OnClickListener, View.OnLongClickListener {

    private var selectedChats = mutableMapOf<Chat, Boolean>()

    override fun getCount() = this.chats.size

    override fun getItem(position: Int) = chats[position]

    override fun getItemId(position: Int): Long {
        return chats.indexOf(chats[position]).toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.tag as? ChatsListItemBinding ?: createBinding(parent.context)
        val chat = getItem(position)

        if (selectedChats[chat] == true) {
            binding.root.background = R.color.primary_dark.toDrawable()
        } else {
            binding.root.background = R.color.background_dark.toDrawable()
        }
        binding.root.tag = chat
        binding.time.text = chat.time
        binding.lastMessage.text = chat.messages.last().value
        binding.userName.text = chat.user.userName
        binding.userIcon.setImageBitmap(chat.user.userImage.userImageBitmap)
        if (chat.count == 0) {
            binding.stateTextIndicator.visibility = View.GONE
            binding.stateIconIndicator.apply {
                setImageDrawable(R.drawable.readed.toDrawable())
                visibility = View.VISIBLE
            }
        } else {
            binding.stateIconIndicator.visibility = View.GONE
            binding.stateTextIndicator.apply {
                text = chat.count.toString()
                visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onClick(v: View) {
        onOpenClickListener.invoke(v.tag as Chat)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLongClick(v: View): Boolean {
        val chat = v.tag as Chat
        selectedChats.merge(chat, selectedChats[chat] ?: true) { _, _ ->
            selectedChats[chat]?.not() ?: true
        }
        notifyDataSetChanged()
        return true
    }

    private fun createBinding(context: Context): ChatsListItemBinding {
        val binding = ChatsListItemBinding.inflate(LayoutInflater.from(context))
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
        binding.root.tag = binding
        return binding
    }
}