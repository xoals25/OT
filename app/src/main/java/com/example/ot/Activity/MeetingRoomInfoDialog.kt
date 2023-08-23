package com.example.ot.Activity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ot.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_roominfo.view.*
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_share.*
/**
 * 2021.03.29
 * 설명: MeetingRoomActivity에서 즉 회의방에서, 상단 바에서 OT클릭하게되면 방의 정보를 보여주는 다이얼로그
 *
 * @author kevin
 * @version 1.0, 권한받기
 * @see None
 */
class MeetingRoomInfoDialog(var context: MeetingRoomActivity) : BottomSheetDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(),R.style.NewDialog).apply { setCanceledOnTouchOutside(true) }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super .onCreateView(inflater, container, savedInstanceState)
        val v:View = inflater.inflate(R.layout.alert_dialog_meetingroom_roominfo, container, false)
        v.textview_alert_meetingroom_room_id.text = context.roomNum;
        v.textview_alert_meetingroom_host.text = context.roomCreateUserName;
        v.textview_alert_meetingroom_pw.text = context.roomPassWord;

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}