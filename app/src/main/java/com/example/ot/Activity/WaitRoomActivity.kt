package com.example.ot.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ot.R
import com.example.ot.Socket.MeetingRoomSignalingClient
import kotlinx.android.synthetic.main.alert_dialog_room_finish_notifi.view.*

/**
 *  2021/04/25 유저가 방에 참가 할 때, 해당 방이 자동수락이 false(off)라면 현재 클래스에 들어와서 호스트의 수락을 기다린다.
 *  즉, 방 참가하기전 대기방이다.
 *
 * @author kevin
 * @see None
 *
 * 2021/04/29
 * 현재는 사용되지 않는 클래스
 * 참여하려는 방에 자동수락이 되어 있지 않으면 원래는 이 클래스를 거쳐서 이동했는데
 * 현재는 참여하려는 방에 들어가서 대기하는 것으로 변경되어있다.
 */

class WaitRoomActivity : AppCompatActivity(),MeetingRoomSignalingClient.CallbackWaitListener {
    var TAG = "WaitRoomActivity"
    var userNickName: String? = null
    var userProfileImgPath: String? = null
    var roomNum:String? = null

    var roomPassWord:String? = null
    var roomCreateUserName:String? = null
    var userUniqueValue:String? = null
    var userEmail:String? = null
    var socialPath:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_room)
        init()
    }

    fun init(){
        intentValueSet()
        MeetingRoomSignalingClient.get()!!.waitRoomInit()
        MeetingRoomSignalingClient.get()!!.setCallbackWaitListener(this)
        waitUserNotifiToHost(userNickName!!, roomNum!!, userProfileImgPath!!,userUniqueValue!!)
    }

    /**
     * 유저가 대기방에 들어가면 방장에게 알려주기위한 메소드
     * @param userNickName 은 방장에게 누가 입장했는지 알려주기 위함
     *
     * */
    fun waitUserNotifiToHost(userNickName: String, roomNum: String, profileImgPath: String,userUniqueValue:String){
        MeetingRoomSignalingClient.get()!!.waitUserNotifiToHost(userNickName,
            roomNum,
            profileImgPath,userUniqueValue)
    }

    /**
     * 방에 참가할때, 넘어오는 intent값 넣어주기
     * JoinMainFragActivity -> RoomEnterMethod -> WaitRoomActivity
     * */
    fun intentValueSet(){
        if(intent.hasExtra("roomNum")){
            userNickName = intent.getStringExtra("userName")//
            userProfileImgPath = intent.getStringExtra("userProfileImgPath")//
            roomNum = intent.getStringExtra("roomNum")//
            roomPassWord= intent.getStringExtra("roomPassWord")//
            roomCreateUserName = intent.getStringExtra("roomCreateUserName")//
            userUniqueValue = intent.getStringExtra("userUniqueValue")//
            userEmail = intent.getStringExtra("userEmail")//
            socialPath = intent.getStringExtra("socialPath")//


        }
    }

    /**
     * 대기방을 나가는 이벤트
     * */
    fun waitRoomExit(view: View) {

    }

    override fun onWaitResponse(resultValue: String) {
        if(resultValue=="accept"){
            var intent = Intent(this, MeetingRoomActivity::class.java)
            intent.putExtra("roomNum", roomNum)
            intent.putExtra("roomPassWord", roomPassWord)
            intent.putExtra("roomCreateUserName", roomCreateUserName)
            intent.putExtra("hostUniqueValue", "")
            intent.putExtra("userUniqueValue", userUniqueValue)
            intent.putExtra("userName", userNickName)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("socialPath", socialPath)
            intent.putExtra("userProfileImgPath", userProfileImgPath)
            startActivity(intent)
            finish()
        }
        else if(resultValue=="refuse"){
            val intent = Intent()
            setResult(RESULT_OK, intent) //결과를 저장
            finish() //액티비티 종료
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MeetingRoomSignalingClient.get()!!.waitRoomInitOff()
    }

}