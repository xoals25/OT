package com.example.ot.Activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ot.*
import com.example.ot.Activity.Fragment.ScreenShareGiverMeetingRoomFragActivity
import com.example.ot.Activity.Fragment.ScreenShareReceiverMeetingRoomFragActivity
import com.example.ot.Activity.Fragment.UsersImgMeetingRoomFragActivity
import com.example.ot.Adapter.MeetingRoomPagerFragmentAdapter
import com.example.ot.Adapter.PeerConnectionAdapter
import com.example.ot.Adapter.SdpAdapter
import com.example.ot.CanvasDepository.ScreenShareGiverCanvasSettings
import com.example.ot.CanvasDepository.ScreenShareGiverDrawerView
import com.example.ot.CanvasDepository.ScreenShareReceiverCanvasSettings
import com.example.ot.Data.ChatItem
import com.example.ot.Data.MeetingRoomUsersData
import com.example.ot.Data.MeetingRoomWaitUsersData
import com.example.ot.Service.WebrtcService
import com.example.ot.Socket.MeetingRoomSignalingClient
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_meeting_room_sub_chatting.*
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_meetingroom_frag_share_receiver.*
import kotlinx.android.synthetic.main.alert_dialog_host_waituser_accept_and_refuse.view.*
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_more_menu.view.*
import kotlinx.android.synthetic.main.alert_dialog_room_finish_notifi.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timer
import com.example.ot.HandlerTypeCode
import com.example.ot.MeetingRoomHandler
import com.example.ot.R
import kotlinx.android.synthetic.main.alert_dialog_meetingroom_more_menu.view.*

/**
 * 2021.03.22
 * 설명: 회의방에 입장하면 보여지는 메인 화면
 * 화면공유,음성채팅,참가자 목록,채팅 등등
 *
 * @author kevin
 * @version 1.0, 선택메뉴 onoff
 * @see None
 */

class MeetingRoomActivity : AppCompatActivity(),
    MeetingRoomSignalingClient.CallbackMeetingRoomListener,
    MeetingRoomSignalingClient.CallbackWaitListener {

    val TAG = "MeetingRoomActivity"
    private val WAIT_USER_NOTI_DIALOG_REQUEST_CODE: Int = 2
    var waitUserNotiDialog: Dialog? = null

    /*기타 뷰 관련*/
    var meetingRoomHandler: MeetingRoomHandler? = null
    var selectMenuLayoutOnOffCk: Boolean = true

    /*뷰페이저 관련*/
    var fragadapter: MeetingRoomPagerFragmentAdapter? = null
    var screenShareGiverActivity: ScreenShareGiverMeetingRoomFragActivity? = null
    var screenSHareReceiverActivity: ScreenShareReceiverMeetingRoomFragActivity? = null
    var usersImgMeetingRoomFragActivity: UsersImgMeetingRoomFragActivity? = null
    var fragmentArrayList = ArrayList<Fragment>()
    /*socket.io 관련*/

    /*공유 관련*/
    var roomOwnerCk = false // true : 방장 O ,false : 방장 X (내가 방장인지 체크)
    var sharePossibleCk = false // true : 공유 가능, false : 공유 불가능 (방장만 공유가능한지 체크)
    var shareWhoCk = false // true : 공유중 O, flase : 공유중 X  (누군가 공유중인지 체크)
    var shareMeck = false // true : 내가 공유중 O, false : 내가 공유중 X

    /*방 정보 관련*/
    lateinit var roomNum: String
    lateinit var roomPassWord: String
    lateinit var roomCreateUserName: String
    lateinit var hostUniqueValue: String

    /*방에 입장한 유저 본인 정보*/
    lateinit var userUniqueValue: String
    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var socialPath: String
    lateinit var userProfileImgPath: String
    private var hostCk: String? = null
    private var enterCk: Boolean = false //방에 입장하면 true 대기화면에 있다면 false

    /*방에 입장할 대기자 유저 관련*/
    var waitUserArrayListActivity: ArrayList<MeetingRoomWaitUsersData> =
        ArrayList<MeetingRoomWaitUsersData>()

    /*webrtc관련 (화면 공유)*/
    private val CAPTURE_PERMISSION_REQUEST_CODE: Int = 1
    private var mMediaProjectionPermissionResultData: Intent? = null
    private var mMediaProjectionPermissionResultCode: Int? = null
    var meetingRoomSignalingClient: MeetingRoomSignalingClient? = null
    var peerConnectionFactory: PeerConnectionFactory? = null
    var peerConnection: PeerConnection? = null
    var shareScreenView: SurfaceViewRenderer? = null
    var mediaStream: MediaStream? = null
    var voiceMediaStream: MediaStream? = null   //작성자: 정근영
    var eglBaseContext: EglBase.Context? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    var videoSource: VideoSource? = null
    var videoCapturer: VideoCapturer? = null
    var peerConnectionMap: HashMap<String, PeerConnection>? = null
    var voicePeerConnectionMap: HashMap<String, PeerConnection>? = null //작성자: 정근영
    var iceServers: MutableList<PeerConnection.IceServer>? = null
    var remoteVideoTrackBag: VideoTrack? = null
    var videoTrack: VideoTrack? = null
    var width: Int? = null
    var height: Int? = null
    var statusbarHeight: Int? = null

    /*브로드 캐스트 리시버*/
    var mAlertReceiver: BroadcastReceiver? = null

    /*주석 기능 관련*/
    var shareScreenShareGiverDrawerView: ScreenShareGiverDrawerView? = null
    var screenShareGiverCanvasSettings: ScreenShareGiverCanvasSettings? = null
    var screenShareReceiverCanvasSettings: ScreenShareReceiverCanvasSettings? = null
    var drawerSituation = "stop"

    /* 채팅 관련*/
    var chatList: ArrayList<ChatItem> = ArrayList<ChatItem>()

    //채팅 페이지로 입장했는지 확인 true: 채팅 입장 o ,false: 채팅 입장 x
    //이걸 해주는 이유 내가 채팅페이지로 갔을경우 MeetingRoomActivity의 상대방들의 메인 프로필에 채팅 알림 이미지 안띄워주기 위함
    var chatActivitySwitchCk: Boolean = false
    var meetingRoomMoreMenuDialog: MeetingRoomMoreMenuDialog? = null
    var userChatCountNotMe: Int = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetingroom)
        meetingRoomSignalingClient = MeetingRoomSignalingClient.get()!!
        meetingRoomSignalingClient!!.socketConnectInit()

//        initSettings()
        initIntentDataSet()
        initUserEnterSet()


    }

    /**
     * 현재 클래스에 입장할 경우 intent.로 넘어온 데이터 변수에 초기화해주기
     * */
    fun initIntentDataSet() {
        /*입장한사람의 정보를 셋팅해준다.*/
        if (intent.hasExtra("userName")) {
            userUniqueValue = intent.getStringExtra("userUniqueValue").toString()
            userName = intent.getStringExtra("userName").toString()
            userEmail = intent.getStringExtra("userEmail").toString()
            socialPath = intent.getStringExtra("socialPath").toString()
            userProfileImgPath = intent.getStringExtra("userProfileImgPath").toString()
        }
        /*
        * 방정보를 가져온다.
        * 방 고유번호, 방 비밀번호, 방 개설자 이름, 호스트 유니크값
        * 방 고유 번호,방 비밀번호,방 개설자 이름 -> 방정보에 표시해주기 위함
        * 호스트 유니크값
        * -> 처음에 방만든 유저가 호스트이므로 방 개설자만 방개설자의 유니크값으로 넣어주고 나중에 들어온 유저는 ""빈값으로 갖고있는다.
        * -> 그리고 나중에 호스트를 변경하면 자신의 hostUniqueValue은 ""빈값으로 만들어주고 소켓으로 변경할 유저에게 알려줘서 변경할 유저의 hostUniqueValue을 변경할 유저의 유니크값을 넣어준다.
        * */
        if (intent.hasExtra("roomNum")) {
            roomNum = intent.getStringExtra("roomNum").toString()
            roomPassWord = intent.getStringExtra("roomPassWord").toString()
            roomCreateUserName = intent.getStringExtra("roomCreateUserName").toString()
            hostUniqueValue = intent.getStringExtra("hostUniqueValue").toString()
        }
    }

    /**
     * 유저가 방에 입장하면 구분해주는 메소드
     * 1.방을 만든 host인지 방에 참가한 client인지 구분
     * 2.링크(초대)로 들어온 client인지 아닌지 구분
     * 2-1. 2번으로 들어온 유저를 기준으로 현재 방이 자동수락인지 아닌지 구분
     * 3.client입장시 현재 방이 자동 수락인지 아닌지 구분
     *
     * host라면 대기화면 gone시켜주고 main화면 Visible 해주고 그외 코드들 동작
     * 어플로 참가한 client중 방이 자동수락일 경우, 바로 대기화면 gone,main화면 visible 해주고 그 외 코드들 동작
     * 어플로 참가한 client중 방이 자동수락이 아닐 경우, 대기화면 띄워주고 방장의 수락 거절을 기다린다.
     *  - 수락 : 대기화면 gone,main화면 visible 해주고 그 외 코드들 동작
     *  - 거절 : 방이 나가진다.
     *  링크로 참가한 client중 방이 자동수락일 경우, 위와 같다.
     *  링크로 참가한 client중 방이 자동수락이 아닐 경우, 위와 같다.
     * */
    fun initUserEnterSet() {
        if (intent.hasExtra("hostCk")) {
            enterCk = true
            hostCk = "host"
            initScreenVisibleSet()
            initSettings()
        } else {
            //참가자
            //초대로 들어왔을 경우
            if (intent.scheme != null && intent.scheme == "ot") {
                Log.d(TAG, "initUserEnterSet: 확인작업 입니다. ot 들어온거 확인")
                /*서버에서 받을 데이터들 roomNum,roomPassWord,roomCreateUserName,hostUniqueValue*/
                roomNum = intent.data!!.getQueryParameter("roomNum") as String
                roomPassWord = intent.data!!.getQueryParameter("roomPassWord") as String
                roomCreateUserName = intent.data!!.getQueryParameter("roomCreateUserName") as String
                hostUniqueValue = intent.data!!.getQueryParameter("hostUniqueValue") as String
                /*shared로 받아올 데이터들 userUniqueValue userName userEmail socialPath userProfileImgPath */
                val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
                userName = pref.getString("name", "0")!!
                userEmail = pref.getString("email", "0")!!
                userProfileImgPath = pref.getString("profileImgPath", "0")!!
                userUniqueValue = pref.getString("uniqueValue", "0")!!
                socialPath = pref.getString("socialPath", "0")!!

                Log.d(TAG, "initUserEnterSet: true 확인 ${intent.data!!.getQueryParameter("autoAccept")}")
                //autoAccept = on
                if (intent.data!!.getQueryParameter("autoAccept") == "true") {
                    Log.d(TAG, "initUserEnterSet: true 확인")
                    enterCk = true
                    initScreenVisibleSet()
                    initSettings()
                }
                //autoAccept = off
                else {
                    meetingRoomSignalingClient!!.waitRoomInit()
                    meetingRoomSignalingClient!!.setCallbackWaitListener(this)
                    timer(period = 100, initialDelay = 100) {
                        if (meetingRoomSignalingClient!!.socket!!.id() != null) {
                            meetingRoomSignalingClient!!.waitUserNotifiToHost(userName,
                                roomNum,
                                userProfileImgPath, userUniqueValue)
                            cancel()
                        }
                    }
                }
            } else {
                if (intent.getStringExtra("autoAccept") == "on") {
                    enterCk = true
                    initScreenVisibleSet()
                    initSettings()
                } else if (intent.getStringExtra("autoAccept") == "off") {
                    meetingRoomSignalingClient!!.waitRoomInit()
                    meetingRoomSignalingClient!!.setCallbackWaitListener(this)
                    timer(period = 100, initialDelay = 100) {
                        if (meetingRoomSignalingClient!!.socket!!.id() != null) {
                            meetingRoomSignalingClient!!.waitUserNotifiToHost(userName,
                                roomNum,
                                userProfileImgPath, userUniqueValue)
                            cancel()
                        }
                    }
                }
            }
            hostCk = "client"
        }
    }

    /**
     * 방에 입장하면 메인 화면 VISIBLE 해주고 대기화면 GONE 해주기 (대기화면을 없애주는 기능)
     * */
    private fun initScreenVisibleSet() {
        framelayout_meetingroom_main_screen.visibility = View.VISIBLE
        constlayout_meetingroom_wait_screen.visibility = View.GONE
    }

    /**
     * 작성자: 정근영
     */
    private fun voiceChatting(){
        voicePeerConnectionMap = HashMap<String, PeerConnection>()

        val audioSource = peerConnectionFactory!!.createAudioSource(MediaConstraints())
        val audioTrack = peerConnectionFactory!!.createAudioTrack("101", audioSource)
        voiceMediaStream = peerConnectionFactory!!.createLocalMediaStream("mediaStream")
        voiceMediaStream!!.addTrack(audioTrack)
    }

    /*초기 설정 값들*/ //->초기설정에 뭐가 있는지 모르겠다.  //설정 변수마다 어떤건지 모르겠다.
    //주석 안에 메소드명 넣고 무슨 메소드인지 설명도 써주기
    private fun initSettings() {
        findingSize() //디바이스 사이즈 구하기
        initScreenShare() //화면 공유 기능 관련 메소드

        Log.d("tag", "회의방에 들어옴")
        meetingRoomHandler = MeetingRoomHandler(
            constlayout_meetingroom_top_view,
            constlayout_meetingroom_bottom_view,
            tablayout_meetingroom,
            this,
            fragadapter!!
        )
        sendDelayMessage()
        /*음소거 버튼 onoff*/
        constlayout_meetingroom_mike_parentview.setOnClickListener {
            removeMessage()
            sendDelayMessage()
        }
        /*공유 버튼*/
        constlayout_meetingroom_share_parentview.setOnClickListener {
            if (shareMeck) {
                meetingRoomSignalingClient!!.screenShareOff()
                screenShareGiverCanvasSettings?.removeFloatingBtnAndSketchBook()
            } else {
                if (shareWhoCk) {
                    Toast.makeText(this, "누군가 공유중이라 공유불가 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val meetingRoomShareDialog = MeetingRoomShareDialog(this)
                    meetingRoomShareDialog.show(supportFragmentManager, meetingRoomShareDialog.tag)
                }
            }
        }
        /*참가자목록 버튼*/
        constlayout_meetingroom_participant_parentview.setOnClickListener {
            val intent = Intent(this, MeetingRoomUserListActivity::class.java)
            intent.putExtra("userUniqueValue", userUniqueValue)
            intent.putExtra("hostCk", hostCk)
            intent.putExtra("roomNum", roomNum)
            intent.putParcelableArrayListExtra("participantUserList",
                usersImgMeetingRoomFragActivity!!.user_list)
            intent.putParcelableArrayListExtra("waitUserList", waitUserArrayListActivity)
            startActivityForResult(intent, WAIT_USER_NOTI_DIALOG_REQUEST_CODE)
        }
        /*위아래에 위치한 선택 메뉴 onoff해주기위한 메소드*/
        framelayout_meetingroom_mainlayout.setOnClickListener {
            setSelectMenuLayoutOnOff()
        }
        /*더보기 클릭 할 경우*/
        constlayout_meetingroom_more_parentview.setOnClickListener {
            meetingRoomMoreMenuDialog = MeetingRoomMoreMenuDialog(this, chatList)
            meetingRoomMoreMenuDialog!!.show(supportFragmentManager,
                meetingRoomMoreMenuDialog!!.tag)
        }
        /*OT v 클릭하면 방정보 보여주는 메소드*/
        linearlayout_meetingroom_info_btn.setOnClickListener {
            val meetingRoomInfoDialog = MeetingRoomInfoDialog(this)
            meetingRoomInfoDialog.isCancelable = true
            meetingRoomInfoDialog.show(supportFragmentManager, meetingRoomInfoDialog.tag)
//            meetingRoomInfoDialog.dismissAllowingStateLoss()
        }
//        viewPager2_meetingroom.setOnClickListener {setSelectMenuLayoutOnOff()}
    }

    /*디바이스 사이즈 Width,Height,Status Bar Height 구하기*/
    private fun findingSize() {
        var display: Display? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = applicationContext.display!!
        } else {
            @Suppress("DEPRECATION")
            display = windowManager.defaultDisplay
        }
        val size = Point()
        display!!.getRealSize(size) // or getSize(size)
        width = size.x
        height = size.y
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusbarHeight = resources.getDimensionPixelSize(resourceId)
            Log.d(TAG, "findingSize: statusbarHeight : $statusbarHeight")
        }
    }

    /*화면공유 초기 설정*/ //초기설정에 뭐가 있는지 모르겠따.
    fun initScreenShare() {
        initViewPagerSettings() //뷰페이저 초기 설정

        peerConnectionMap = HashMap<String, PeerConnection>()
        iceServers = mutableListOf()
        iceServers!!.add(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        eglBaseContext = EglBase.create().eglBaseContext
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(this)
                .createInitializationOptions()
        )
        var options: PeerConnectionFactory.Options = PeerConnectionFactory.Options()
        var defaultVideoEncoderFactory: DefaultVideoEncoderFactory = DefaultVideoEncoderFactory(
            eglBaseContext,
            true,
            true
        )

        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()
//방을 만든 사람이면 따로 기본 방 설정 정보를 서버에 보내주기
        if (intent.hasExtra("hostCk")) {
            val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
            val autoAccept = pref.getString("autoAccept", "false")
            meetingRoomSignalingClient!!.roomInitDataSend(autoAccept!!)
            hostCk = "host"
        } else {
            hostCk = "client"
        }
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
//        videoSource = peerConnectionFactory!!.createVideoSource(true)
        Log.d(TAG, "initScreenShare: 순서 확인 5-5")
        videoSource = peerConnectionFactory!!.createVideoSource(true)
        Log.d(TAG, "initScreenShare: 순서 확인 5-4")
        //서비스에게 받을 브로드캐스트 리시버
        mAlertReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                var fromServiceData = intent.getStringExtra("message")
                if (fromServiceData == "start") {
                    screenshare()
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mAlertReceiver!!,
            IntentFilter("MoveServiceFilter")
        )

        /*소켓 연결하는 부분*/
        MeetingRoomSignalingClient.get()?.setCallbackMeetingRoomListener(this)
        meetingRoomSignalingClient = MeetingRoomSignalingClient.get()!!
        meetingRoomSignalingClient!!.setRoomInit(roomNum,
            userName,
            userProfileImgPath,
            userUniqueValue)

        voiceChatting() //작성자: 정근영
        meetingRoomSignalingClient!!.meetingInit()

    }

    /*현재 메서드는 화면 클릭시 상황에 따라 선택 메뉴들을 OnOff 해준다 */
    fun setSelectMenuLayoutOnOff() {
        if (selectMenuLayoutOnOffCk) {
            selectMenuLayoutOnOffCk = false
            constlayout_meetingroom_top_view.visibility = View.GONE
            constlayout_meetingroom_bottom_view.visibility = View.GONE
            tablayout_meetingroom.visibility = View.VISIBLE
            removeMessage()
        } else {
            selectMenuLayoutOnOffCk = true
            constlayout_meetingroom_top_view.visibility = View.VISIBLE
            constlayout_meetingroom_bottom_view.visibility = View.VISIBLE
            tablayout_meetingroom.visibility = View.GONE
            sendDelayMessage()
        }
    }

    /*따로 화면을 클릭 하지 않아도 몇초후에 뷰가 사라지게 해준다.*/
    private fun sendDelayMessage() {
        val msg: Message =
            meetingRoomHandler!!.obtainMessage(HandlerTypeCode.MeetingRoomAcitvityType.VIEW_CHANGE_MESSAGE)
        meetingRoomHandler!!.sendMessageDelayed(msg, 3500)
    }

    /*핸들러 메시지 딜레이 취소*/
    private fun removeMessage() {
        meetingRoomHandler!!.removeMessages(HandlerTypeCode.MeetingRoomAcitvityType.VIEW_CHANGE_MESSAGE)
    }

    /*뷰페이저 초기 설정 해주는 것들*/
    private fun initViewPagerSettings() {
        fragadapter = MeetingRoomPagerFragmentAdapter(this, this, fragmentArrayList)
        viewPager2_meetingroom.adapter = fragadapter
        usersImgMeetingRoomFragActivity = UsersImgMeetingRoomFragActivity(this)
        var meetingRoomUsersData: MeetingRoomUsersData? = null
        if (intent.hasExtra("hostCk")) {
            meetingRoomUsersData =
                MeetingRoomUsersData(userName, userProfileImgPath, userUniqueValue, "host", "off")
        } else {
            meetingRoomUsersData =
                MeetingRoomUsersData(userName, userProfileImgPath, userUniqueValue, "client", "off")
        }
        usersImgMeetingRoomFragActivity!!.addUser(meetingRoomUsersData)
        usersImgMeetingRoomFragActivity!!.usersListAdapterDataSetChanged()
        fragadapter!!.addFragment(usersImgMeetingRoomFragActivity!!)
        TabLayoutMediator(tablayout_meetingroom, viewPager2_meetingroom) { tab, position ->
        }.attach()
        viewPager2_meetingroom.offscreenPageLimit = fragmentArrayList.size
    }

    /**
     * 공유를 시작하면 아이콘
     * @param shareOnOff 는 공유 시작을 알리는 것인지 공유 중지인지 알려준다. true=공유 시작 false=공유 멈춤
     * */
    fun changeShareIconText(shareOnOff: Boolean) {
        //공유중 -> 공유 x (아이콘 및 텍스트는 공유 가능상태로 변경)
        if (!shareOnOff) {
            shareMeck = false
            imgaeView_meetingroom_share.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.meetingroom_share_icon_effect
                )
            )
            textview_meetingroom_share.setTextColor(
                resources.getColorStateList(
                    R.color.meetingroom_share_click_text_effect,
                    applicationContext.theme
                )
            )
            textview_meetingroom_share.text = "공유"
        }
        //공유 x-> 공유 o (아이콘 및 텍스트는 공유 불가 상태로 변경)
        else {
            imgaeView_meetingroom_share.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.meetingroom_share_stop_icon_effect
                )
            )
            textview_meetingroom_share.setTextColor(
                resources.getColorStateList(
                    R.color.meetingroom_share_stop_click_text_effect,
                    applicationContext.theme
                )
            )
            textview_meetingroom_share.text = "공유 중지"
        }
    }

    /**
     * 화면 공유 시작 메소드
     * 한번더 예외처리를 해주고 화면공유시작
     * */
    fun screen_share_start() {
        if (shareWhoCk) {
            Toast.makeText(this, "누군가 공유중이라 공유불가 합니다.", Toast.LENGTH_SHORT).show()
        } else {
            startScreenCapture()
        }

    }

    /**
     * 작성자: 정근영
     */
    @Synchronized
    private fun voiceGetOrCreatePeerConnection(socketId: String): PeerConnection? {
        var voicePeerConnection = voicePeerConnectionMap!![socketId]
        if (voicePeerConnection != null) {
            return voicePeerConnection
        }
        voicePeerConnection = peerConnectionFactory!!.createPeerConnection(
            iceServers,
            object : PeerConnectionAdapter(
                "PC:$socketId") {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate!!)
                    meetingRoomSignalingClient!!.voiceSendIceCandidate(iceCandidate, socketId)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    super.onAddStream(mediaStream)
//                    val remoteVideoTrack = mediaStream.videoTracks[0]
                    runOnUiThread {}
                }
            })
        Log.d(TAG, "voiceGetOrCreatePeerConnection: 순서 확인 11")
        voicePeerConnection!!.addStream(voiceMediaStream)
        Log.d(TAG, "voiceGetOrCreatePeerConnection: 순서 확인 22")
        voicePeerConnectionMap!![socketId] = voicePeerConnection
        return voicePeerConnection
    }

    //이 메소드가 무슨 메소드인지 모르겠다.
    @Synchronized
    private fun getOrCreatePeerConnection(socketId: String): PeerConnection? {
        var peerConnection = peerConnectionMap!![socketId]
        if (peerConnection != null) {
            return peerConnection
        }
        Log.d(TAG, "getOrCreatePeerConnection: 순서 확인 10")
        peerConnection = peerConnectionFactory!!.createPeerConnection(
            iceServers,
            object : PeerConnectionAdapter("PC:$socketId") {

                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate)
                    meetingRoomSignalingClient!!.sendIceCandidate(iceCandidate, socketId)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    super.onAddStream(mediaStream)
                    val remoteVideoTrack = mediaStream.videoTracks[0]
                    remoteVideoTrackBag = remoteVideoTrack
                    if (!shareMeck) {
                        runOnUiThread {
                            remoteVideoTrack.addSink(
                                screenSHareReceiverActivity!!.receiverScreenShareView!!
                            )
                        }
                    }
                }
            })
        Log.d(TAG, "getOrCreatePeerConnection: 순서 확인 11")
        peerConnection!!.addStream(mediaStream)
        Log.d(TAG, "getOrCreatePeerConnection: 순서 확인 12")
        peerConnectionMap!![socketId] = peerConnection
        return peerConnection
    }

    /*공유할 화면 데이터 설정 (공유하는사람이 사용하는 메소드)*/
    fun screenshare() {
        videoCapturer = createScreenCapturer()!!
//        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer!!.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource!!.capturerObserver
        )
        videoCapturer!!.startCapture(480, 640, 30)

        // create VideoTrack
        videoTrack = peerConnectionFactory!!.createVideoTrack("100", videoSource)
        mediaStream = peerConnectionFactory!!.createLocalMediaStream("mediaStream")
        mediaStream!!.addTrack(videoTrack)
        meetingRoomSignalingClient!!.screenShareCreateAndJoin()
        meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.SCREEN_SHARE_ICON_ON_CHANGE_MESSAGE)


        /*홈화면으로 가지고, 주석 관련 레이아웃 만들어 준다*/
        screenShareGiverCanvasSettings = ScreenShareGiverCanvasSettings(this)
        screenShareGiverCanvasSettings!!.goHome()
        screenShareGiverCanvasSettings!!.createFloatingBtnAndSketchBook()
//        val intent = Intent(Intent.ACTION_MAIN) //태스크의 첫 액티비티로 시작
//        intent.addCategory(Intent.CATEGORY_HOME) //홈화면 표시
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
//        startActivity(intent)
    }

    @TargetApi(21)
    private fun createScreenCapturer(): VideoCapturer? {
        return if (this.mMediaProjectionPermissionResultCode != RESULT_OK) {
            null
        } else {
            ScreenCapturerAndroid(
                this.mMediaProjectionPermissionResultData, object : MediaProjection.Callback() {
                    override fun onStop() {
                        Log.d(TAG, "onStop: 거절 확인")
                    }
                })
        }
    }

    @TargetApi(21)
    private fun startScreenCapture() {
        val mediaProjectionManager = application.getSystemService(
            MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), this.CAPTURE_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * 화면 공유가 중지될 때 동작하는 메소드
     * 초기 presenter, viewer에 따라 다르게 설정해준다.
     * @param user 는 화면 공유한 사람=presenter, 화면 공유 받은 사람=viewer로 받아오는값이다.
     */
    private fun onShareWebRtcSettingsOff(user: String) {
        if (user == "presenter") {
            videoCapturer!!.stopCapture()
            videoCapturer!!.dispose()//presenter
//            videoCapturer.initialize(null,null,null)
        } else if (user == "viewer") {
            //이거 따로 안해줘도 그냥 screenSHareReceiverActivity==null해주면 되는거 같다. 아니다 해줘야 했던거 같다.
            remoteVideoTrackBag!!.removeSink(screenSHareReceiverActivity!!.receiverScreenShareView) //뷰어
            screenSHareReceiverActivity!!.receiverScreenShareView!!.release() //뷰어
        }


        for (key in peerConnectionMap!!.keys) {
            peerConnectionMap!![key]!!.removeStream(mediaStream)
            peerConnectionMap!![key]!!.dispose()
        }
        peerConnectionMap!!.clear()
        mediaStream!!.removeTrack(videoTrack)
    }

    private fun onShareStop(user: String) {
        if (user == "presenter") {
            meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.SCREEN_SHARE_ICON_OFF_CHANGE_MESSAGE)
            shareMeck = false
            onShareWebRtcSettingsOff(user)
            fragadapter!!.removeFragmentHelperHandler(screenShareGiverActivity!!)
            screenShareGiverActivity = null
            //서비스 stopfourground해주기
            val intent = Intent(this, WebrtcService::class.java)
            stopService(intent)
        } else if (user == "viewer") {
//            if(frameLayout_meetingroom_receiver_draw_canvas.visibility==View.VISIBLE){
//                frameLayout_meetingroom_receiver_draw_canvas.visibility = View.GONE
//            }
            shareWhoCk = false
            drawerSituation = "stop"
            onShareWebRtcSettingsOff(user)
            fragadapter!!.removeFragmentHelperHandler(screenSHareReceiverActivity!!)
            screenSHareReceiverActivity!!.onDestroy()
            screenSHareReceiverActivity = null
        }
    }

    /*화면 공유 시작할 경우 메소드*/
    fun onDrawerStart() {
        meetingRoomSignalingClient!!.drawerStart()
    }

    fun onDrawerStop() {
        meetingRoomSignalingClient!!.drawerStop()
    }

    fun drawerStartSetting() {
        if (screenSHareReceiverActivity != null) {
            this.drawerSituation = "start"
            screenShareReceiverCanvasSettings = ScreenShareReceiverCanvasSettings(this)
            screenShareReceiverCanvasSettings!!.createSketchBook()
//        meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.CanvasType.SCREEN_SHARE_RECEIVER_DRAWER_OPEN)
            runOnUiThread {
                if (view_meetingroom_receiver_drawer_tools.visibility == View.GONE) {
                    view_meetingroom_receiver_drawer_tools.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode != this.CAPTURE_PERMISSION_REQUEST_CODE) {
            return
        }
        else*/ if (requestCode == this.CAPTURE_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            this.mMediaProjectionPermissionResultCode = resultCode
            this.mMediaProjectionPermissionResultData = data
            meetingRoomSignalingClient!!.screenShareOn(width!!, height!!, statusbarHeight!!)
        }
        Log.d(TAG, "onActivityResult: 확인 작업입니다.!!")
        if (requestCode == this.WAIT_USER_NOTI_DIALOG_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult:  확인 작업입니다.!!")
            waitUserNotiDialog?.dismiss()
            waitUserNotiDialog = null
        }

    }

    override fun onEnterCallback(jsonObject: JSONObject?, jsonArray: JSONArray?) {
        Log.d(TAG, "onEnterCallback: 확인확인확인 onEnterCallback")
        //누군가 방에 입장시, 입장한 유저의 데이터를 받아와서 UsersImgMeetingRoomFragActivity의 어레이리스트 추가
        if (jsonObject != null) {
            val userName = jsonObject.optString("userName")
            val userProfileImgPath = jsonObject.optString("userProfileImgPath")
            val userUniqueValue = jsonObject.optString("userUniqueValue")
            val hostAndClient = jsonObject.optString("hostAndClient")
            val meetingRoomUsersData = MeetingRoomUsersData(userName,
                userProfileImgPath,
                userUniqueValue,
                hostAndClient, "off")
            usersImgMeetingRoomFragActivity!!.addUser(meetingRoomUsersData)
            runOnUiThread {
                if (usersImgMeetingRoomFragActivity!!.user_list!!.size == 2) {
                    /*코드변경 spanCount 2->1*/
                    usersImgMeetingRoomFragActivity!!.gridLayoutManager = GridLayoutManager(this, 2)
                    usersImgMeetingRoomFragActivity!!.userlistRecyclerview!!.layoutManager =
                        usersImgMeetingRoomFragActivity!!.gridLayoutManager
                }
                usersImgMeetingRoomFragActivity!!.usersListAdapterDataSetChanged()
            }
        }
        //내가 방에 입장시, 방에 입장시 유저들의 데이터를 받아옴 UsersImgMeetingRoomFragActivity의 어레이리스트 추가
        else if (jsonArray != null) {
            for (i in 0 until jsonArray!!.length()) {
                val userName = jsonArray.getJSONObject(i).getString("userName");
                val userProfileImgPath = jsonArray.getJSONObject(i).getString("userProfileImgPath");
                val userUniqueValue = jsonArray.getJSONObject(i).getString("userUniqueValue");
                val hostAndClient = jsonArray.getJSONObject(i).getString("hostAndClient")
                val meetingRoomUsersData = MeetingRoomUsersData(userName,
                    userProfileImgPath,
                    userUniqueValue,
                    hostAndClient, "off")
                usersImgMeetingRoomFragActivity!!.addUser(meetingRoomUsersData)
                Log.d(TAG,
                    "onEnterCallback: 사이즈 확인 ${usersImgMeetingRoomFragActivity!!.user_list!!.size}")
            }
            runOnUiThread {
                /*코드변경 spanCount2->1*/
                usersImgMeetingRoomFragActivity!!.gridLayoutManager = GridLayoutManager(this, 2)
                usersImgMeetingRoomFragActivity!!.userlistRecyclerview?.layoutManager =
                    usersImgMeetingRoomFragActivity!!.gridLayoutManager
//                if(usersImgMeetingRoomFragActivity!!.userlistRecyclerview==null){
//                    usersImgMeetingRoomFragActivity!!.setuserlistRecyclerviewNullCk(false)
//                }
                usersImgMeetingRoomFragActivity!!.usersListAdapterDataSetChanged()
            }
        }

    }

    /**
     * 화면 공유가 시작될 때 동작하는 메소드
     * 초기 presenter, viewer에 따라 다르게 설정해준다.
     * @param user 는 화면 공유한 사람=presenter, 화면 공유 받은 사람=viewer로 받아오는값이다.
     */
    override fun screenShareStart(
        user: String,
        width: Int?,
        height: Int?,
        statusBarHeight: Int?,
        drawerSituation: String?
    ) {
        Log.d(TAG, "screenShareStart: 순서 확인 1")
        if (user == "presenter") {
            shareMeck = true
            screenShareGiverActivity = ScreenShareGiverMeetingRoomFragActivity(this)
            fragadapter!!.addFragmentHelperHandler(screenShareGiverActivity!!)
            if (Build.VERSION.SDK_INT >= 26) {
                val intent = Intent(this, WebrtcService::class.java)
                startForegroundService(intent)
            } else {
                val intent = Intent(this, WebrtcService::class.java)
                startService(intent)
            }
        } else if (user == "viewer") {
            shareWhoCk = true
            screenSHareReceiverActivity = ScreenShareReceiverMeetingRoomFragActivity(
                this,
                eglBaseContext!!,
                width!!,
                height!!,
                statusBarHeight!!
            )
            fragadapter!!.addFragmentHelperHandler(screenSHareReceiverActivity!!)
            videoTrack = peerConnectionFactory!!.createVideoTrack("100", videoSource)
            mediaStream = peerConnectionFactory!!.createLocalMediaStream("mediaStream")
            mediaStream!!.addTrack(videoTrack)
            if (drawerSituation == "start") {
                this.drawerSituation = "start"
            }
        }
    }

    /**
     * 화면 공유가 중지될 때 동작하는 메소드
     * 초기 presenter, viewer에 따라 다르게 설정해준다.
     * @param user 는 화면 공유한 사람=presenter, 화면 공유 받은 사람=viewer로 받아오는값이다.
     */
    override fun onScreenShareStopCallback(user: String) {
        onShareStop(user)
    }

    override fun onScreenShareSelfJoined(presenterSocketid: String) {
        val peerConnection = getOrCreatePeerConnection(presenterSocketid)
        peerConnection?.createOffer(object : SdpAdapter("createOfferSdp:$presenterSocketid") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peerConnection.setLocalDescription(
                    SdpAdapter("setLocalSdp:$presenterSocketid"),
                    sessionDescription
                )
                //viewer ->presenter
                //from viewer -> to presenter
                meetingRoomSignalingClient!!.sendSessionDescription(sessionDescription,
                    presenterSocketid)
            }
        }, MediaConstraints())
    }

    /**
     * 유저가 나갔을때 처리해주는 메소드
     *
     * data에 들어있는 값들
     * room : 나간 유저의 방 번호(없어도 되는듯)
     * from : 나간 유저의 socketId
     * areYouHost : 나간 유저가 host인지,client인지 판별후에 host면 자신도 나가지게 하기
     * isThisSharingUser : 나간 유저가 화면 공유를 하고 있는 유저 였다면 화면 공유 종료시켜주기
     * */
    override fun onScreenSharePeerLeave(data: JSONObject) {
        Log.d(TAG, "onPeerLeave : $data")
        //여기서 유저 나간거 처리해주기
        val exitUserRoomNum = data.optString("room")
        val exitUserSocketId = data.optString("from")
        val areYoutHost = data.optString("areYouHost")
        val isThisSharingUser = data.optString("isThisSharingUser")
        //공유한사람이 나간거라면
        if (isThisSharingUser == "yes") {
            onShareStop("viewer")
        }
        //주인장이 나간거라면
        if (areYoutHost == "host") {
            finish()
        } else {
            //사용자 리사이클러뷰 제거 해주는 단계
            val exitUserUniqueValue = data.optString("userUniqueValue")
            usersImgMeetingRoomFragActivity!!.removeUser(exitUserUniqueValue)
            runOnUiThread {
                if (usersImgMeetingRoomFragActivity?.user_list?.size == 1) {
                    usersImgMeetingRoomFragActivity?.gridLayoutManager = GridLayoutManager(this, 1)
                    usersImgMeetingRoomFragActivity?.userlistRecyclerview?.layoutManager =
                        usersImgMeetingRoomFragActivity?.gridLayoutManager
                }
                usersImgMeetingRoomFragActivity?.usersListAdapterDataSetChanged()
            }
        }
    }

    override fun onScreenShareOfferReceived(data: JSONObject, to: String) {
        runOnUiThread {
            val peerConnection = getOrCreatePeerConnection(to)
            peerConnection!!.setRemoteDescription(
                SdpAdapter("setRemoteSdp:$to"),
                SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp"))
            )
            peerConnection!!.createAnswer(object : SdpAdapter("localAnswerSdp") {
                override fun onCreateSuccess(sdp: SessionDescription) {
//                    socketArrayList.add(to);
                    //presenter ->viewer
                    //from presenter -> to viewer
                    super.onCreateSuccess(sdp!!)
                    peerConnectionMap!![to]!!.setLocalDescription(SdpAdapter("setLocalSdp:$to"),
                        sdp)
                    meetingRoomSignalingClient!!.sendSessionDescription(sdp!!, to)
                }
            }, MediaConstraints())
        }
    }

    override fun onScreenShareAnswerReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection!!.setRemoteDescription(
            SdpAdapter("setRemoteSdp:$socketId"),
            SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp"))
        )
    }

    override fun onScreenShareIceCandidateReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection!!.addIceCandidate(
            IceCandidate(
                data.optString("id"),
                data.optInt("label"),
                data.optString("candidate")
            )
        )
    }


    /*공유한사람이 주석작성 시작 하게되면*/
    override fun onDrawerStartSocket() {
        //핸들러로 보내줘야 할듯
        drawerStartSetting()
    }

    /*공유한사람이 주석작성 중지하면*/
    override fun onDrawerStopSocket() {
        screenShareReceiverCanvasSettings = null
        this.drawerSituation = "stop"
//        meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.CanvasType.SCREEN_SHARE_RECEIVER_DRAWER_CLOSE)
        runOnUiThread {
            //만약 내 주석이 켜져있다면 GONE처리 해주기
            if (frameLayout_meetingroom_receiver_draw_canvas.visibility == View.VISIBLE) {
                frameLayout_meetingroom_receiver_draw_canvas.visibility = View.GONE
                constlayout_meetingroom_receiver_canvas_start_btn.visibility = View.VISIBLE
                meetingroom_receiver_canvas_drawer_item.visibility = View.GONE
            }
//            if(framelayout_meetingroom_receiver_pen_toolbox.visibility==View.VISIBLE){
//                framelayout_meetingroom_receiver_pen_toolbox.visibility = View.INVISIBLE
//            }
            if (framelayout_meetingroom_receiver_color_toolbox.visibility == View.VISIBLE) {
                framelayout_meetingroom_receiver_color_toolbox.visibility = View.INVISIBLE
            }
            if (framelayout_meetingroom_receiver_width_toolbox.visibility == View.VISIBLE) {
                framelayout_meetingroom_receiver_width_toolbox.visibility = View.INVISIBLE
            }

            if (view_meetingroom_receiver_drawer_tools.visibility == View.VISIBLE) {
                view_meetingroom_receiver_drawer_tools.visibility = View.GONE
            }

            viewPager2_meetingroom.isUserInputEnabled = true
        }
    }

    /*화면 공유 받은사람이 주석을 작성하면 공유해준 사람의 화면에 주석을 그려주는 콜백메소드*/
    override fun onDrawerSendFromScreenShareReceiver(
        jsonArray: JSONArray?,
        color: String?,
        paintWidth: Float, screenShareReceiverViewWidth: Int, screenShareReceiverViewHeight: Int
    ) {
        if (color == "black") {
            shareScreenShareGiverDrawerView!!.friend_paint.xfermode = null
            shareScreenShareGiverDrawerView!!.friend_paint.color = Color.BLACK
        } else if (color == "red") {
            shareScreenShareGiverDrawerView!!.friend_paint.xfermode = null
            shareScreenShareGiverDrawerView!!.friend_paint.color = Color.RED
        } else if (color == "yellow") {
            shareScreenShareGiverDrawerView!!.friend_paint.xfermode = null
            shareScreenShareGiverDrawerView!!.friend_paint.color = Color.YELLOW
        } else if (color == "blue") {
            shareScreenShareGiverDrawerView!!.friend_paint.xfermode = null
            shareScreenShareGiverDrawerView!!.friend_paint.color = Color.parseColor("#00a8f3")
        } else if (color == "transparente") {
            Log.d(TAG, "onDrawerSendFromScreenShareReceiver: transparente")
            shareScreenShareGiverDrawerView!!.friend_paint.color = Color.TRANSPARENT
            shareScreenShareGiverDrawerView!!.friend_paint.xfermode =
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
//        shareScreenShareGiverDrawerView!!.friend_paint.strokeWidth = paintWidth
//        shareScreenShareGiverDrawerView!!.friend_paint.strokeWidth = (paintWidth* screenShareReceiverViewWidth.toFloat()!!)/width!!
        shareScreenShareGiverDrawerView!!.friend_paint.strokeWidth =
            (paintWidth * width!!) / screenShareReceiverViewWidth.toFloat()
        for (i in 0 until jsonArray!!.length()) {
            try {
                if (i + 1 < jsonArray.length()) {
                    val startX = jsonArray.getJSONObject(i).getDouble("X")
                    val startY = jsonArray.getJSONObject(i).getDouble("Y")
                    val stopX = jsonArray.getJSONObject(i + 1).getDouble("X")
                    val stopY = jsonArray.getJSONObject(i + 1).getDouble("Y")
                    val startXChange = startX * width!! / screenShareReceiverViewWidth.toFloat()
                    val startYChange = startY * height!! / screenShareReceiverViewHeight.toFloat()
                    val stopXChange = stopX * width!! / screenShareReceiverViewWidth.toFloat()
                    val stopYChange = stopY * height!! / screenShareReceiverViewHeight.toFloat()

                    if (startX == stopX && startY == stopY) {
                        shareScreenShareGiverDrawerView!!.canvas!!.drawPoint(startXChange.toFloat(),
                            startYChange.toFloat(),
                            shareScreenShareGiverDrawerView!!.friend_paint)
                    } else {
                        shareScreenShareGiverDrawerView!!.canvas!!.drawLine(startXChange.toFloat(),
                            startYChange.toFloat(),
                            stopXChange.toFloat(),
                            stopYChange.toFloat(),
                            shareScreenShareGiverDrawerView!!.friend_paint)
                    }
                }
            } catch (e: JSONException) {
                Log.d(TAG, "draw: ERROR MESSAGE : $e")
            }
        }
        meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.CanvasType.DRAW_LINE_INVALIDATE)
    }

    /*방 대기자가 들어오면 동작하는 이벤트*/
    override fun onHostWaitUserResponse(waitUserDataToJsonObject: JSONObject) {
        val waitUserSocketId = waitUserDataToJsonObject.getString("from")
        val waitUserNickName: String = waitUserDataToJsonObject.getString("userNickName")
        val waitUserImgPath: String = waitUserDataToJsonObject.getString("profileImgPath")
        val waitUserUniqueValue: String = waitUserDataToJsonObject.getString("userUniqueValue")

        val meetingRoomWaitUsersData = MeetingRoomWaitUsersData(waitUserNickName,
            waitUserImgPath,
            waitUserUniqueValue,
            "client",
            waitUserSocketId)
        waitUserArrayListActivity.add(meetingRoomWaitUsersData!!)

        Log.d(TAG,
            "onHostWaitUserResponse: waitUserArrayListActivity 사이즈 확인1 ${waitUserArrayListActivity.size}")
        if (hostCk == "host") {
            val builder = AlertDialog.Builder(this)
            val view: View = LayoutInflater.from(this)
                .inflate(R.layout.alert_dialog_host_waituser_accept_and_refuse, null, false)
            builder.setView(view)
            runOnUiThread {
                waitUserNotiDialog = builder.create()
                view.textview_dialog_host_waituser.text =
                    getString(R.string.waitUserNickname, waitUserNickName)
                view.button_dialog_wait_user_accept.setOnClickListener {
                    meetingRoomSignalingClient!!.waitUserAcceptOrRefuse(waitUserSocketId,
                        "accept",
                        waitUserUniqueValue)

                    waitUserNotiDialog?.dismiss()
                    waitUserNotiDialog = null
                }
                view.button_dialog_wait_user_refuse.setOnClickListener {
                    meetingRoomSignalingClient!!.waitUserAcceptOrRefuse(waitUserSocketId,
                        "refuse",
                        waitUserUniqueValue)
                    waitUserNotiDialog?.dismiss()
                    waitUserNotiDialog = null
                }
                waitUserNotiDialog!!.show()
            }
        }

    }

    /**
     *  방에 있는 유저들에게 대기자 어레이리스트의 해당 유니크값을 갖고있는 데이터 삭제( 대기자가 수락을 받았든 거절을 받았든 상관없음)
     * */
    override fun onRoomUsersWaitResult(waitUserUniqueValue: String) {
        Log.d(TAG,
            "onRoomUsersWaitResult: waitUserArrayListActivity 사이즈 확인2 ${waitUserArrayListActivity.size}")
        for (i in 0 until waitUserArrayListActivity.size) {
            Log.d(TAG, "onRoomUsersWaitResult: 사이즈 확인 3 $i")
            if (waitUserArrayListActivity[i].uniquenum == waitUserUniqueValue) {
                Log.d(TAG, "onRoomUsersWaitResult: 사이즈 확인 4 $i")
                waitUserArrayListActivity.removeAt(i)
                break
            }
        }
    }

    /**
     * 채팅이 올경우 채팅 내용을 현재 클래스에 있는 채팅 어레이리스트에 담아두기
     * 이유 : 현재 클래스에서 채팅 클래스로 넘어갈 경우 방에 참가한 시점부터 채팅이 온 데이터를 넘겨주기위해
     *
     * */
    override fun onChatting(chatJsonObject: JSONObject) {
        val fromUserNickname = chatJsonObject.getString("fromUserNickname")
        val fromUserSocketId: String = chatJsonObject.getString("fromUserSocketId")
        val fromUserUniqueNum: String = chatJsonObject.getString("fromUserUniqueNum")
        val content: String = chatJsonObject.getString("content")
        if (fromUserSocketId == meetingRoomSignalingClient!!.socket!!.id()) {
            val chatItem = ChatItem(fromUserNickname, content, ChatType.RIGHT_MESSAGE)
            chatList.add(chatItem)
        } else {
            val chatItem = ChatItem(fromUserNickname, content, ChatType.LEFT_MESSAGE)
            chatList.add(chatItem)

            if (!chatActivitySwitchCk) {
                userChatCountNotMe += 1;
                runOnUiThread {
                    if (userChatCountNotMe <= 99) {
                        meetingRoomMoreMenuDialog?.v?.textview_chat_count?.text =
                            userChatCountNotMe.toString()
                    } else {
                        meetingRoomMoreMenuDialog?.v?.textview_chat_count?.text = "99+"
                    }

                    if (meetingRoomMoreMenuDialog?.v?.textview_chat_count?.visibility == View.INVISIBLE) {
                        meetingRoomMoreMenuDialog?.v?.textview_chat_count?.visibility = View.VISIBLE
                    }
                }
                for (i in 1 until usersImgMeetingRoomFragActivity!!.user_list!!.size) {
                    if (usersImgMeetingRoomFragActivity!!.user_list!![i].uniquenum == fromUserUniqueNum) {
                        Log.d(TAG, "onChatting: 유저 데이터 확인 1")
                        Log.d(TAG, "onChatting: ${!chatActivitySwitchCk}")

                        if (usersImgMeetingRoomFragActivity!!.user_list!![i].chatNotiOnOff == "off") {
                            usersImgMeetingRoomFragActivity!!.user_list!![i].chatNotiOnOff = "on"
                            runOnUiThread {
                                usersImgMeetingRoomFragActivity!!.usersListAdapter!!.notifyItemChanged(
                                    i)
                            }
                        }
                        break;
                    }

                }
            }
        }
    }

    //작성자: 정근영
    override fun onOfferReceived(data: JSONObject) {
        runOnUiThread {
            val socketId = data.optString("from")
            val peerConnection = voiceGetOrCreatePeerConnection(socketId)
            peerConnection!!.setRemoteDescription(SdpAdapter("setRemoteSdp:$socketId"),
                SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp")))
            peerConnection!!.createAnswer(object : SdpAdapter("localAnswerSdp") {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    super.onCreateSuccess(sdp!!)
                    voicePeerConnectionMap!![socketId]!!
                        .setLocalDescription(SdpAdapter("setLocalSdp:$socketId"), sdp)
                    meetingRoomSignalingClient!!.voiceSendSessionDescription(sdp, socketId)
                }
            }, MediaConstraints())
        }
    }

    //작성자: 정근영
    override fun onAnswerReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = voiceGetOrCreatePeerConnection(socketId)
        peerConnection!!.setRemoteDescription(SdpAdapter("setRemoteSdp:$socketId"),
            SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp")))
    }

    //작성자: 정근영
    override fun onIceCandidateReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = voiceGetOrCreatePeerConnection(socketId)
        peerConnection!!.addIceCandidate(IceCandidate(
            data.optString("id"),
            data.optInt("label"),
            data.optString("candidate")
        ))
    }

    //작성자: 정근영
    override fun onPeerJoined(socketId: String?) {
        val peerConnection = voiceGetOrCreatePeerConnection(socketId!!)
        peerConnection!!.createOffer(object : SdpAdapter("createOfferSdp:$socketId") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription!!)
                peerConnection.setLocalDescription(SdpAdapter("setLocalSdp:$socketId"),
                    sessionDescription)
                meetingRoomSignalingClient!!.voiceSendSessionDescription(sessionDescription,
                    socketId)
            }
        }, MediaConstraints())
    }

    override fun onWaitResponse(resultValue: String) {
        MeetingRoomSignalingClient.get()!!.waitRoomInitOff()
        //정상 작동
        if (resultValue == "accept") {
            Log.d(TAG, "onWaitResponse: 작동 확인 로그입니다.")
            runOnUiThread {
                enterCk = true
                initScreenVisibleSet()
                initSettings()
            }
        } else if (resultValue == "refuse") {
            if (intent.scheme != null && intent.scheme == "ot") {
                if (!intent.hasExtra("userName")) {
                    destroyMethod()
                    Log.d(TAG, "onWaitResponse: 확인작업 입니다.")
                    moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
                    finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
                    android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료
                }
            }
            val intent = Intent()
            setResult(RESULT_OK, intent) //결과를 저장
            finish() //액티비티 종료
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        var meetingRoomExitNotiDialog = MeetingRoomExitNotiDialog(this)
        meetingRoomExitNotiDialog.start()
    }

    fun destroyMethod(){
        if (enterCk) {
            screenShareGiverCanvasSettings?.removeFloatingBtnAndSketchBook()
            usersImgMeetingRoomFragActivity?.user_list = null
            usersImgMeetingRoomFragActivity = null
            //추후에 비정상종료는 서비스로 작업해주고 일단 여기에서 소켓 연결 끊어주자.
            if (meetingRoomSignalingClient != null) {
                //공유관련 데이터 처리해주는 곳 (내가 공유 하고 있을때 처리)
                if (shareMeck) {
                    onShareStop("presenter")
                }
                if (shareWhoCk) {
                    onShareStop("viewer")
                }
                if (hostUniqueValue == userUniqueValue) {
                    meetingRoomSignalingClient!!.exitRoom("host", userUniqueValue)
                } else {
                    meetingRoomSignalingClient!!.exitRoom("client", userUniqueValue)
                }
                LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mAlertReceiver!!
                )
            }
            val intent = Intent(this, WebrtcService::class.java)
            stopService(intent)
            meetingRoomSignalingClient!!.meetingInitOff()
            meetingRoomSignalingClient!!.socket!!.disconnect()
            meetingRoomSignalingClient!!.socket!!.close()

            voiceMediaStream!!.dispose()    //음성 채팅 종료해주는 메소드 (데이터 누수 막아줌)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyMethod()

    }

    override fun onResume() {
        super.onResume()
        chatActivitySwitchCk = false //false 로 해줘서 현재 클래스에 있다는 것을 알리기
    }
}