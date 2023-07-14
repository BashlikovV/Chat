package by.bashlikovv.chat.app.screens.messenger.chats

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import by.bashlikovv.chat.R
import by.bashlikovv.chat.app.struct.Chat
import by.bashlikovv.chat.app.utils.buildTime
import by.bashlikovv.chat.databinding.ChatsListItemBinding
import java.util.Calendar

typealias onChatActionListener = (Chat) -> Unit

class ChatsAdapter(
    private val chats: List<Chat>,
    private val onOpenClickListener: onChatActionListener
) : BaseAdapter(), View.OnClickListener, View.OnLongClickListener {

    private var selectedChats = mutableMapOf<Chat, Boolean>()

    override fun getCount() = this.chats.size

    override fun getItem(position: Int) = chats[position]

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
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
        binding.time.text = buildTime(chat.time)
        binding.lastMessage.text = if (chat.messages.last().value.isNotEmpty()) {
            "${chat.messages.last().user.userName}: ${chat.messages.last().value}"
        } else {
            ""
        }
        binding.userName.text = chat.user.userName
        binding.userIcon.setImageBitmap(chat.user.userImage.userImageBitmap)
        if (chat.count == 0) {
            binding.stateTextIndicator.visibility = View.GONE
        } else {
            binding.stateTextIndicator.apply {
                text = chat.count.toString()
                visibility = View.VISIBLE
            }
        }
        if (Calendar.getInstance().time.time - chat.user.lastConnectionTime.time < 300000) {
            binding.userActivity.setImageBitmap(R.drawable.avatar_badge.getBitmapFromImage(parent))
        } else {
            binding.userActivity.setImageBitmap(R.drawable.avatar_unfilled_badge.getBitmapFromImage(parent))
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

    private fun Int.getBitmapFromImage(parent: ViewGroup): Bitmap {
        val db = ContextCompat.getDrawable(parent.context, this)
        val bit = Bitmap.createBitmap(
            db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bit)
        db.setBounds(0, 0, canvas.width, canvas.height)
        db.draw(canvas)

        return bit
    }
}