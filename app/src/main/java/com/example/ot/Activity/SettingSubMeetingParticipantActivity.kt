package com.example.ot.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ot.R

/**
 * 2021.03.12
 * 설명: 참여하고 있는 회의 화면에서 참가자 버튼을 눌렀을 경우 전환되고,
 *      현재 회의에 참여되어 있는 사용자들과 추가적으로 사용자를 추가할 수 있는 화면이 있는 클래스
 * 작성자: socical
 */

class SettingSubMeetingParticipantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_sub_meeting_participant)
    }
}