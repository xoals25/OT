package com.example.ot.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ot.Data.ChatItem
import com.example.ot.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_more_menu.view.*
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_share.*

class MeetingRoomMoreMenuDialog(var context: MeetingRoomActivity,var chatList:ArrayList<ChatItem>) : BottomSheetDialogFragment() {
    var v: View? =null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(),
            R.style.NewDialog).apply { setCanceledOnTouchOutside(true) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v=inflater.inflate(R.layout.alert_dialog_meetingroom_more_menu, container, false)
        clickStore(v!!)
        if(context.userChatCountNotMe>0){
            if(context.userChatCountNotMe<=99){
                v!!.textview_chat_count.text = context.userChatCountNotMe.toString()
            }
            else{
                v!!.textview_chat_count.text = "99+"
            }
            v!!.textview_chat_count.visibility = View.VISIBLE
        }
        return v
    }

    /** 더보기에서 채팅을 클릭했을 때 채팅방으로 이동하게 해주는 메소드 */
    private fun clickStore(v: View) {
        v.constlayout_chat_btn.setOnClickListener {
            var intent = Intent(context, MeetingRoomSubChattingActivity::class.java)
            intent.putExtra("roomNum", context.roomNum)
            intent.putExtra("uniqueNum",context.userUniqueValue)
            intent.putParcelableArrayListExtra("chatList",chatList)
            context.chatActivitySwitchCk = true
            var forListSize = context.usersImgMeetingRoomFragActivity!!.user_list!!.size
            for(i in 1 until forListSize){
                if(context.usersImgMeetingRoomFragActivity!!.user_list!![i].chatNotiOnOff=="on"){
                    context.usersImgMeetingRoomFragActivity!!.user_list!![i].chatNotiOnOff="off"
                }
            }
            context.usersImgMeetingRoomFragActivity!!.usersListAdapter!!.notifyDataSetChanged()
            context.userChatCountNotMe = 0
            if(v!!.textview_chat_count.visibility==View.VISIBLE){
                v!!.textview_chat_count.visibility = View.INVISIBLE
            }
            startActivity(intent)
            context.meetingRoomMoreMenuDialog?.dismiss()
            context.meetingRoomMoreMenuDialog = null
        }
    }
}