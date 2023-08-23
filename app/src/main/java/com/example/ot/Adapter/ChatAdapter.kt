package com.example.ot.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ot.Data.ChatItem
import com.example.ot.ChatType
import com.example.ot.R
import kotlin.collections.ArrayList

class ChatAdapter(var chatList:ArrayList<ChatItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return if (viewType == ChatType.LEFT_MESSAGE) {
            view = inflater.inflate(R.layout.meeting_room_sub_chatting_left_item, parent, false)
            LeftViewHolder(view)
        } else {
            view = inflater.inflate(R.layout.meeting_room_sub_chatting_right_item, parent, false)
            RightViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = chatList[position]
        if (viewHolder is LeftViewHolder) {
            viewHolder.setItem(item)
        } else {
            (viewHolder as RightViewHolder).setItem(item)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun addItem(item: ChatItem) {
        chatList.add(item)
        notifyDataSetChanged()
    }

    fun setItems(items: ArrayList<ChatItem>) {
        this.chatList = items
    }

    fun getItem(position: Int): ChatItem {
        return chatList[position]
    }

    override fun getItemViewType(position: Int): Int {
        return chatList[position].viewType
    }

    inner class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameText: TextView = itemView.findViewById(R.id.name_text)
        var contentText: TextView = itemView.findViewById(R.id.msg_text)
        fun setItem(item: ChatItem) {
            nameText.text = item.name
            contentText.text = item.content
        }

    }

    inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentText: TextView = itemView.findViewById(R.id.msg_text)
        fun setItem(item: ChatItem) {
            contentText.text = item.content
        }

    }
}