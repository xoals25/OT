package com.example.ot.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ot.Activity.Fragment.ScreenShareGiverMeetingRoomFragActivity
import com.example.ot.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_share.*

class MeetingRoomShareDialog(var context: MeetingRoomActivity) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(),R.style.NewDialog).apply { setCanceledOnTouchOutside(true) }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super .onCreateView(inflater, container, savedInstanceState)
        val v:View = inflater.inflate(R.layout.alert_dialog_meetingroom_share, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        constlayout_meetingroom_dialog_screensharebtn.setOnClickListener {
            /*다이얼로그는 열려 있지만 한번더 예외처리 해줘야한다. 공유를 눌렀는데 그 순간 누군가 공유를 할 수도 있으니까.*/
            context.screen_share_start()
            dismiss()
        }
        constlayout_meetingroom_dialog_whiteboardbtn.setOnClickListener {dismiss()}
        textview_alert_meetingroom_cancle.setOnClickListener { dismiss() }
    }

}