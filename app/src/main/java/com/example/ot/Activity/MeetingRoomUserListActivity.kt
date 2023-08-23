package com.example.ot.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ot.Adapter.MeetingRoomSubUsersListAdapter
import com.example.ot.Adapter.MeetingRoomSubWaitUsersListAdapter
import com.example.ot.Data.MeetingRoomUsersData
import com.example.ot.Data.MeetingRoomWaitUsersData
import com.example.ot.R
import com.example.ot.Socket.MeetingRoomSignalingClient
import kotlinx.android.synthetic.main.activity_meeting_room_sub_user_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

/**
 * 2021/04/27
 * MeetingRoomActivity.class의 화면 하단에 참가자 버튼을 누르면 들어오는 클래스
 * 이 클래스는 방에 들어온 참가자, 방에 들어오기전 대기자들을 보여주는 곳이다.
 *
 * @author kevin
 *
 * */

class MeetingRoomUserListActivity : AppCompatActivity(),MeetingRoomSignalingClient.CallbackSubUserListListener {
    private val WAIT_USER_NOTI_DIALOG_REQUEST_CODE: Int = 2
    var userUniqueValue:String? = null
    var hostCk:String? = null
    var roomNum:String? = null
    /*참가자 리사이클러뷰 관련*/
    private var participantUserList: ArrayList<MeetingRoomUsersData>? = null
    private var recyclerViewParticipant:RecyclerView?=null
    private var meetingRoomSubUsersListAdapter:MeetingRoomSubUsersListAdapter?=null
    private var linearLayoutManager:LinearLayoutManager?=null

    /*대기자 리사이클러뷰 관련*/
    private var waitUserList: ArrayList<MeetingRoomWaitUsersData>? = null
    private var recyclerViewWaitUser:RecyclerView?=null
    private var meetingRoomSubWaitUsersListAdapter: MeetingRoomSubWaitUsersListAdapter?=null
    private var linearLayoutManagerToWaitUser:LinearLayoutManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_room_sub_user_list)
        init()
    }

    /**
     * 현재 클래스에서 onCreate에서 실행되어야 할 메소드 모음
     * */
    private fun init(){
        intentDataSet()
        clickStore()
        callbackListenerSet()
        recyclerViewParticipantSet()
        recyclerViewWaitUserSet()
    }
    private fun clickStore(){
        contlayout_invite_link_copy_btn.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("link","http://3.36.140.214/Web/OtInvite.php?roomNum=${roomNum}")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this,"초대 링크가 복사 되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * intent로 넘어온 값들 변수에 초기화
     * userList는 참가자데이터를 갖고있는 어레이리스트인데 MeetingRoomActivity의  UsersImgMeetingRoomFragActivity에서 사용된 어레이리스트 데이터들을 넣어준 것이다.
     * userUniqueValue는 현재 본인의 UniqueValue값인데 가져온 이유는 현클래스에서 사용할 리사이클러뷰에 있는 데이터의 본인 여부를 판단하기위함
     * */
    private fun intentDataSet(){
        participantUserList = intent.getParcelableArrayListExtra("participantUserList")
        waitUserList = intent.getParcelableArrayListExtra("waitUserList")
        if(waitUserList!!.size==0){
            linearlayout_wait_user.visibility = View.GONE
        }
        userUniqueValue = intent.getStringExtra("userUniqueValue")
        roomNum = intent.getStringExtra("roomNum")
        hostCk = intent.getStringExtra("hostCk")
    }
    /**
     *콜백리스너 인터페이스
     * */
    private fun callbackListenerSet(){
        MeetingRoomSignalingClient.get()!!.setCallbackSubUserListListener(this)
    }
    /**
     * ParticipantRecyclerView 셋팅해주는곳
     * 현 클래스의 참가자 목록을 보여주는 리사이클러뷰를 설정
     * */
    private fun recyclerViewParticipantSet(){
        recyclerViewParticipant = recyclerview_user_list_participant
        meetingRoomSubUsersListAdapter = MeetingRoomSubUsersListAdapter(participantUserList!!,this,this)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerViewParticipant!!.adapter = meetingRoomSubUsersListAdapter
        recyclerViewParticipant!!.layoutManager = linearLayoutManager
        meetingRoomSubUsersListAdapter!!.notifyDataSetChanged()
    }

    /**
     * WaitUserRecyclerView 셋팅해주는곳
     * 현 클래스의 방에 참여하기전 수락 거절을 기다리는 대기자 목록을 보여주는 리사이클러뷰를 설정
     * */
    private fun recyclerViewWaitUserSet(){
        recyclerViewWaitUser = recyclerview_user_list_wait_user
        meetingRoomSubWaitUsersListAdapter = MeetingRoomSubWaitUsersListAdapter(waitUserList!!,this,hostCk!!)
        linearLayoutManagerToWaitUser=LinearLayoutManager(this)
        recyclerViewWaitUser!!.adapter = meetingRoomSubWaitUsersListAdapter
        recyclerViewWaitUser!!.layoutManager = linearLayoutManagerToWaitUser
        meetingRoomSubWaitUsersListAdapter!!.notifyDataSetChanged()
    }

    /**
     * participantUserList의 데이터를 지워주기 위함
     * @param uniqueValue 지워줄 유저의 유니크값 : participantUserList에 있는 데이터를 비교해서 uniqueValue와 같은 값을 갖는 데이터 지워준다.
     * */
    private fun removeParticipantDataUserList(uniqueValue:String){
        for ((index,item) in participantUserList!!.withIndex()){
            if(item.uniquenum==uniqueValue){
                participantUserList!!.removeAt(index)
                break
            }
        }
    }

    /**
     * waitUserList의 데이터를 지워주기 위함
     * @param uniqueValue 지워줄 유저의 유니크값 : participantUserList에 있는 데이터를 비교해서 uniqueValue와 같은 값을 갖는 데이터 지워준다.
     * */
    private fun removeWaitUserDataUserList(uniqueValue:String){
        for ((index,item) in waitUserList!!.withIndex()){
            if(item.uniquenum==uniqueValue){
                waitUserList!!.removeAt(index)
                break
            }
        }
    }

    /**
     * 방 나갈때 들어가야할 메소드 - 현재 클래스에서 다시 MeetingRoomActivity클래스로 이동하여 onResume으로 돌아갈 때 onActivityResult가 동작해야해서 넣어준 기능
     * */
    private fun finishForOnActivityResult(){
        var intent = Intent()
        setResult(RESULT_OK,intent)
        finish()
    }

    /**
     * 본인 혹은 타유저가 들어오면 실행하는 메소드
     * @param jsonObject 타 유저가 들어오면 타유저의 데이터를 갖고 있는 객체( 1명의 데이터)
     * @param jsonArray 내가 방에 입장하면 방에 있는 유저들의 데이터를 갖고 있는 객체(0명 이상의 데이터)
     * */
    override fun onEnterCallback(jsonObject: JSONObject?, jsonArray: JSONArray?) {
        //타 유저의 데이터 (1명의 데이터)
        if(jsonObject!=null){
            val userName = jsonObject.optString("userName")
            val userProfileImgPath = jsonObject.optString("userProfileImgPath")
            val userUniqueValue = jsonObject.optString("userUniqueValue")
            val hostAndClient = jsonObject.optString("hostAndClient")
            val meetingRoomUsersData = MeetingRoomUsersData(userName,userProfileImgPath,userUniqueValue,hostAndClient,"off")
            participantUserList!!.add(meetingRoomUsersData)
            runOnUiThread {
                meetingRoomSubUsersListAdapter!!.notifyDataSetChanged()
            }
        }
        //내가 방에 입장시, 방에 입장시 유저들의 데이터를 받아옴 UsersImgMeetingRoomFragActivity의 어레이리스트 추가
        else if(jsonArray!=null){
            for (i in 0 until jsonArray!!.length()) {
                val userName = jsonArray.getJSONObject(i).getString("userName")
                val userProfileImgPath = jsonArray.getJSONObject(i).getString("userProfileImgPath")
                val userUniqueValue = jsonArray.getJSONObject(i).getString("userUniqueValue")
                val hostAndClient = jsonArray.getJSONObject(i).getString("hostAndClient")
                val meetingRoomUsersData = MeetingRoomUsersData(userName,userProfileImgPath,userUniqueValue,hostAndClient,"off")
                participantUserList!!.add(meetingRoomUsersData)
            }
            runOnUiThread {
                meetingRoomSubUsersListAdapter!!.notifyDataSetChanged()
            }
        }
    }
    /**
     * 자동 수락이 꺼져있을 때 대기자가 발생하는데 대기자가 들어오면 실행하는 메소드
     * @param waitUserDataToJsonObject 대기자의 정보를 갖고있다.
     * */
    override fun onWaitUserEnterCallback(waitUserDataToJsonObject: JSONObject) {

        val waitUserSocketId = waitUserDataToJsonObject.getString("from")
        val waitUserNickName: String = waitUserDataToJsonObject.getString("userNickName")
        val waitUserImgPath:String =waitUserDataToJsonObject.getString("profileImgPath")
        val waitUserUniqueValue:String =waitUserDataToJsonObject.getString("userUniqueValue")
        val meetingRoomWaitUsersData=MeetingRoomWaitUsersData(waitUserNickName,waitUserImgPath,waitUserUniqueValue,"client",waitUserSocketId)
        waitUserList!!.add(meetingRoomWaitUsersData!!)
        runOnUiThread {
            linearlayout_wait_user.visibility = View.VISIBLE
            meetingRoomSubWaitUsersListAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 대기자 유저 리스트에서 대기자 삭제 해주기 위함
     *
     * */
    override fun onRoomUsersWaitResult(waitUserUniqueValue: String) {
        removeWaitUserDataUserList(waitUserUniqueValue)
        runOnUiThread {
            if(waitUserList!!.size==0){
                linearlayout_wait_user.visibility = View.GONE
            }
            meetingRoomSubWaitUsersListAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 타 유저가 나가면 실행하는 메소드
     * @param data 타유저의 데이터를 갖고 있다.
     * */
    override fun onByeCallback(data: JSONObject) {
        //사용자 리사이클러뷰 제거 해주는 단계
        val exitUserUniqueValue = data.optString("userUniqueValue")
        removeParticipantDataUserList(exitUserUniqueValue)
        runOnUiThread {
            meetingRoomSubUsersListAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finishForOnActivityResult()
    }
}