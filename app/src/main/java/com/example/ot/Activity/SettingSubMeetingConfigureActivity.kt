package com.example.ot.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ot.R

/**
 * 2021.03.12
 * 설명: 회의 설정(마이크 음소거, 오디오 연결, 자동 수락, 회의 암호)에 대한 화면이 있는 클래스
 * 작성자: socical
 */

class SettingSubMeetingConfigureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_sub_meeting_configure)
    }
}