package com.example.ot.Activity

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import com.example.ot.Activity.Http.Room.RoomExitMethod
import com.example.ot.R
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_leave.*

class MeetingRoomExitNotiDialog(var meetingRoomActivity: MeetingRoomActivity) {

    private val dialog = Dialog(meetingRoomActivity)
    var button:Button? = null
    fun start(){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_dialog_meetingroom_leave)

        if(meetingRoomActivity.hostUniqueValue==meetingRoomActivity.userUniqueValue){
            //방장이 나갈경우 텍스트 셋팅
            dialog.button_meeting_leave.text = "회의 종료하고 나가기"
        }
        else{
            //유저가 나갈경우 텍스트 셋팅
            dialog.button_meeting_leave.text = "회의 나가기"
        }

        dialog.button_meeting_leave.setOnClickListener {
            if(meetingRoomActivity.hostUniqueValue==meetingRoomActivity.userUniqueValue){
                dialog.dismiss()
                RoomExitMethod.roomExit(meetingRoomActivity.roomNum,meetingRoomActivity)
            }
            else {
                dialog.dismiss()
                meetingRoomActivity.finish()
            }

        }
        dialog.button_meeting_leave_cancel.setOnClickListener {
            dialog.dismiss()
        }
        //확인 버튼 눌렀을 경우 소켓통신 + dismiss 처리 finish처리 해주기
        //취소 버튼 눌렀을 경우 dismiss처리
        dialog.show()
    }

}