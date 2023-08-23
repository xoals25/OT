package com.example.ot.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ot.Adapter.ChatAdapter
import com.example.ot.Data.ChatItem
import com.example.ot.ChatType
import com.example.ot.Data.MessageData
import com.example.ot.R
import com.example.ot.Socket.MeetingRoomSignalingClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_meeting_room_sub_chatting.*

class MeetingRoomSubChattingActivity : AppCompatActivity() {

    var meetingRoomSignalingClient: MeetingRoomSignalingClient? = null //msocket을 이걸루 변경

    var roomNumber: String? = null
    var userName: String? = null
    var uniqueNum : String? =null
    var adapter: ChatAdapter? = null
    val gson = Gson()
    var chatList:ArrayList<ChatItem>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_room_sub_chatting)
        intentDataSetInit()
        chattingInit()
        recyclerView_chatting_screen.scrollToPosition(adapter!!.itemCount - 1)
    }

    /**
     * intent로 넘어온 데이터 처리 해주는 곳
     *
     * */

    private fun intentDataSetInit(){
        chatList = intent.getParcelableArrayListExtra("chatList")
    }

    /**
     * 채팅
     */
    private fun chattingInit() {

        val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
        roomNumber = intent.getStringExtra("roomNum")
        userName = pref.getString("name", "0")
        uniqueNum = intent.getStringExtra("uniqueNum")
        adapter = ChatAdapter(chatList!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView_chatting_screen.layoutManager = layoutManager
        recyclerView_chatting_screen.adapter = adapter

        // 메시지 전송 버튼
        imageButton_chatting_send.setOnClickListener { sendMessage() }
        meetingRoomSignalingClient = MeetingRoomSignalingClient.get()
        meetingRoomSignalingClient!!.socket!!.on("update") { args: Array<Any> ->
            val data = gson.fromJson(args[0].toString(),
                MessageData::class.java)
            addChat(data)
        }
    }

    // 리사이클러뷰에 채팅 추가
    private fun addChat(data: MessageData) {

        runOnUiThread {
            if (data.type == "MESSAGE") {
                adapter!!.addItem(ChatItem(data.fromUserNickname,
                    data.content,
                    ChatType.LEFT_MESSAGE))
                recyclerView_chatting_screen.scrollToPosition(adapter!!.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        meetingRoomSignalingClient!!.socket!!.emit("newMessage", gson.toJson(MessageData("MESSAGE",
            userName.toString(),
            meetingRoomSignalingClient!!.socket!!.id(),
            uniqueNum!!,
            roomNumber.toString(),
            editText_chatting_content.text.toString())))
        adapter!!.addItem(ChatItem(userName.toString(),
            editText_chatting_content.text.toString(),
            ChatType.RIGHT_MESSAGE))
        recyclerView_chatting_screen.scrollToPosition(adapter!!.itemCount - 1)
        editText_chatting_content.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        meetingRoomSignalingClient!!.socket!!.off("update")
    }
}