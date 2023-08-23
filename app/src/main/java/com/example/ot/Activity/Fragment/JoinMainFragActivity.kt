package com.example.ot.Activity.Fragment

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ot.Activity.Http.Room.RoomEnterMethod
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_join_main_frag.view.*
import kotlinx.android.synthetic.main.alert_dialog_host_waituser_accept_and_refuse.view.*
import kotlinx.android.synthetic.main.alert_dialog_main_frag_join.view.*
import kotlinx.android.synthetic.main.alert_dialog_room_finish_notifi.view.*
import java.util.*
import kotlin.concurrent.timer


/**
 * 2021.03.12
 * 설명: 초대 받은 회의에 참여하거나 참여하기 버튼을 눌러 회의에 참여할 수 있는 화면이 있는 클래스
 * 작성자: socical
 */

class JoinMainFragActivity : Fragment() {
    lateinit var v: View;

    /*유저 정보 관련*/
    var userName: String? = null
    lateinit var userEmail: String
    lateinit var userUniqueValue: String
    lateinit var roomPassWord: String
    lateinit var userImgPath: String
    lateinit var socialPath: String
    lateinit var joinMainFragActivity: JoinMainFragActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.activity_join_main_frag, container, false)
        initData();
        v.button_join_main_frag_joinbtn.setOnClickListener(MeetingNewMeetingBtn())
        joinMainFragActivity = this

        return v
    }

    /*초기에 변수 데이터 초기화 해주는 곳*/
    private fun initData() {
        val pref = context!!.getSharedPreferences("loginInfo", AppCompatActivity.MODE_PRIVATE)
        userName = pref.getString("name", "0")!!
        userEmail = arguments!!["userEmail"].toString()
        userUniqueValue = arguments!!["userUniqueValue"].toString()
        roomPassWord = pref.getString("roomPassWord", "0")!!
        userImgPath = arguments!!["userProfileImgPath"].toString()
        socialPath = arguments!!["userSocialPath"].toString()
    }


    /*
    * 추후에 정리할것 참여하기 버튼누르면 방 정보 입력하는 다이얼로그가 뜬다
    * 여기에서 레트로핏 통신해서 방 정보 있는지 받아올 것이다.
    * */
    inner class MeetingNewMeetingBtn : View.OnClickListener {
        override fun onClick(v: View) {
            val builder = AlertDialog.Builder(context!!)
            val view: View = LayoutInflater.from(context!!)
                .inflate(R.layout.alert_dialog_main_frag_join, null, false)
            builder.setView(view)
            view.editText_join_user_name.setText(userName)
            val dialog = builder.create()
            view.button_meeting_creating.setOnClickListener { // 4. 사용자가 입력한 내용을 가져와서
                val roomUniqueValue = view.editText_join_room_unique_value.text.toString()
                val roomPassWord = view.editText_join_room_password.text.toString()

                val userName = view.editText_join_user_name.text.toString()
                when {
                    roomUniqueValue == "" -> {
                        Toast.makeText(
                            context!!,
                            "회의 ID를 입력해주세요.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    roomPassWord == "" -> {
                        Toast.makeText(
                            context!!,
                            "방 비밀번호를 입력해주세요.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    userName == "" -> {
                        Toast.makeText(
                            context!!,
                            "닉네임을 입력해주세요.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        RoomEnterMethod.roomEnter(
                            roomUniqueValue,
                            roomPassWord,
                            userUniqueValue,
                            userEmail,
                            userName,
                            socialPath,
                            userImgPath,
                            dialog,
                            context!!,
                            joinMainFragActivity
                        )
                    }
                }
            }
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            openNotifiDialog()
        }
    }

    fun openNotifiDialog(){
        //거절당했다는 문구와 함께 finish해주기
        val builder = AlertDialog.Builder(context!!)
        var second = 5
        val view: View = LayoutInflater.from(context!!)
            .inflate(R.layout.alert_dialog_room_finish_notifi, null, false)
        builder.setView(view)
        val dialog = builder.create()
        val mTask:Timer = timer(period = 1000){
            activity!!.runOnUiThread { view.textview_dialog_ck_btn.text = "확인 (${second--})" }
            if(second<=0){
                dialog.dismiss()
                cancel()
            }
        }
        view.textview_dialog_notifi.text = "호스트가 귀하의 참가를 거절했습니다."
        view.textview_dialog_ck_btn.setOnClickListener {
            mTask.cancel()
            dialog.dismiss()
        }


        builder.setCancelable(false)
        dialog.show()
    }
}