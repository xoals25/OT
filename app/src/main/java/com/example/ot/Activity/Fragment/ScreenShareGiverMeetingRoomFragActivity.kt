package com.example.ot.Activity.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.R
/**
 * 누군가 핸드폰화면 공유를 한다면 뷰페이저에 추가되어 공유한 화면을 볼 수 있는 클래스이다.
 *
 * @author kevin
 * @version 1.0, 공유한 상대화면 띄워주기
 * @see None

 */
class ScreenShareGiverMeetingRoomFragActivity(var meetingRoomActivity: MeetingRoomActivity) :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:View = inflater.inflate(R.layout.activity_meetingroom_frag_share_giver, container, false)
        v.setOnClickListener {
            meetingRoomActivity.setSelectMenuLayoutOnOff()
        }
        return v
    }
}