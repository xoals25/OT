package com.example.ot.Activity.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ot.Activity.Http.Room.RoomCreateMethod
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_main_frag_meeting.*
import kotlinx.android.synthetic.main.activity_main_frag_meeting.view.*
import java.util.*

/**
 * 회의방 입장하는 클래스
 * 설명 현재까지 기능들 :
 * @author kevin
 * @version 1.0, 방 만들기 서버와 통신
 * @see None
 */

/**
 * 2021.03.20
 * 설명: 방 나누기 토이 프로젝트 kotlin.으로 변환해서 옮김
 * 작성자: socical
 */


class MeetingMainFragActivity : Fragment() {
    val TAG = "MeetingMainFragActivity"
    lateinit var v: View

    /*유저 정보 관련*/
    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var uniqueValue: String
    lateinit var roomPassWord: String
    lateinit var userImgPath: String
    lateinit var socialPath: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.activity_main_frag_meeting, container, false)
        init(v)
        val pref = context!!.getSharedPreferences("loginInfo", AppCompatActivity.MODE_PRIVATE)
        userName = pref.getString("name", "0")!!
        userEmail = arguments!!["userEmail"].toString()
        uniqueValue = arguments!!["userUniqueValue"].toString()
        roomPassWord = pref.getString("roomPassWord", "0")!!
        userImgPath = arguments!!["userProfileImgPath"].toString()
        socialPath = arguments!!["userSocialPath"].toString()

        v.textView_meeting_privateId.text = uniqueValue;
        return v
    }

    private fun init(v: View) {
        v.button_meeting_yes.setOnClickListener(MeetingNewMeetingBtn())
    }

    /**
     *
     * 회의 만들기 화면에서 예버튼 누르면 나오는 메소드
     * RoomCreateMethod.roomCreate()는 방 만들기 레트로핏 통신 하는 메소드이다.
     * @param uniqueValue 는 roomUniqueValue로 방 고유번호를 서버로 전송할 값
     * @param uniqueValue 는 ownerUniqueValue로 누가 만든방인지 알려주는 데이터 서버로 전송할 값
     * @param roomPassWord 는 roomPassWord로 방비밀번호를 서버로 전송할 값
     * @param userEmail 는 유저이메일 회의방으로 전달해줄 값
     * @param userName 는 유저이름,호스트 이름(여기는 회의방 만드는 클래스이므로 여기서 입장하는 사람은 호스트이기 때문에 같게 해놓았다)
     * @param socialPath 는 유저가 가입한 경로(email로만으로는 유저를 식별하는데 카카오,구글에서 중복될 수 있기때문에 또 받음)
     * @param userImgPath 는 유저 프로필 이미지 경로
     * */
    inner class MeetingNewMeetingBtn : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.button_meeting_yes -> {
                    val pref =
                        context!!.getSharedPreferences("loginInfo", AppCompatActivity.MODE_PRIVATE)
                    val autoAccept: String = pref.getString("autoAccept", "").toString()
                    Log.d(TAG, "onClick:autoAccept :  ${autoAccept}")
                    RoomCreateMethod.roomCreate(uniqueValue,
                        uniqueValue,
                        autoAccept,
                        roomPassWord,
                        userEmail,
                        userName,
                        socialPath,
                        userImgPath,
                        context!!)
                }
            }
        }
    }


}