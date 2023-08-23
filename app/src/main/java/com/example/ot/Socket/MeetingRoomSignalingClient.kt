package com.example.ot.Socket

import SocketSSL
import android.util.Log
import com.example.ot.Remote.EventInput
import com.example.ot.ServerIp
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.*


class MeetingRoomSignalingClient {
    companion object{
        var instance:MeetingRoomSignalingClient?=null
        fun get() : MeetingRoomSignalingClient? {
            if(instance==null){
                synchronized(MeetingRoomSignalingClient::class){
                    if(instance==null){
                        instance = MeetingRoomSignalingClient()
                    }
                }
            }
            return instance
        }
        fun delete(){
            instance = null
        }
    }
    val TAG :String ="SignalingClient"
    private val options : IO.Options = IO.Options()
    var presenterSocketid :String=""
    var iceCandidate:Boolean = true
    var roomnum:String? = null
    var userName:String? = null
    var userProfileImgPath:String? = null
    var userUniqueValue:String? = null
    var socket:Socket?=null
    private var callbackMeetingRoomListener:CallbackMeetingRoomListener? = null
    private var callbackSubUserListListener:CallbackSubUserListListener? = null
    private var callbackWaitListener:CallbackWaitListener? = null
    private var input: EventInput = EventInput()



    /**
     *  현재 클래스에서 사용할 데이터 셋팅 해주는곳
     *  MeetingRoomActivity에 갖고 있는데이터를 사용하며, init()를 선언하기 전에 해준다.
     *  @param roomnum 유저가 참여한 방번호
     *  @param userName 유저의 닉네임
     *  @param userProfileImgPath 유저의 이미지 경로
     *  @param userUniqueValue 유저의 고유값
     * */
    fun setRoomInit(
        roomnum: String,
        userName: String,
        userProfileImgPath: String,
        userUniqueValue: String
    ){
        this.roomnum = roomnum
        this.userName = userName
        this.userProfileImgPath = userProfileImgPath
        this.userUniqueValue = userUniqueValue
    }

    fun setCallbackMeetingRoomListener(callbackMeetingRoomListener: CallbackMeetingRoomListener){
        this.callbackMeetingRoomListener = callbackMeetingRoomListener
    }
    fun setCallbackSubUserListListener(callbackSubUserListListener: CallbackSubUserListListener){
        this.callbackSubUserListListener = callbackSubUserListListener
    }
    fun setCallbackWaitListener(callbackWaitListener: CallbackWaitListener){
        this.callbackWaitListener = callbackWaitListener
    }
    fun socketConnectInit(){
        SocketSSL.set(options)
        socket = IO.socket(ServerIp.STREAM_HOST)
        socket!!.connect()
//        userIdRegisterSocket()

    }

    fun roomEnter(){
        roomEnter(userName!!, userProfileImgPath!!, userUniqueValue!!)
    }

    fun meetingInit(){

        try {
//            SocketSSL.set(options)
//            socket = IO.socket(STREAM_HOST)
//            socket.connect()
            roomEnter()
            socket!!.on("enter", onEnter)
            socket!!.on("screenShareJoined", onScreenShareJoined)
            socket!!.on("bye", onBye)
            socket!!.on("screenShareMessage", onScreenShareMessage)
            socket!!.on("shareStart", onShareStart)
            socket!!.on("shareStop", onShareStop)
            socket!!.on("drawerStart", onDrawerStart)
            socket!!.on("drawerStop", onDrawerStop)
            socket!!.on("drawerSendFromScreenShareReceiver", onDrawerSendFromScreenShareReceiver)
            socket!!.on("roomUsersWaitUserResponse", onRoomUsersWaitUserResponse)
            socket!!.on("roomUsersWaitResult", onRoomUsersWaitResult)
            socket!!.on("chatting", onChatting)

            /**
             * 작성자: 정근영
             * 음성 채팅과 관련된 메소드
             */
            socket!!.on("message") { args: Array<Any?> ->
                Log.e("chao", "message " + args.contentToString())
                val arg = args[0]
                if (arg is String) {
                } else if (arg is JSONObject) {
                    when (arg.optString("type")) {
                        "offer" -> {
                            Log.d("1","111111111111")
                            callbackMeetingRoomListener?.onOfferReceived(arg)
                        }
                        "answer" -> {
                            Log.d("2","2222222222222")
                            callbackMeetingRoomListener?.onAnswerReceived(arg)
                        }
                        "candidate" -> {
                            Log.d("3","333333333333333")
                            callbackMeetingRoomListener?.onIceCandidateReceived(arg)
                        }
                    }
                }
            }

            /**
             * 작성자: 정근영
             */
            socket!!.on("join") { args: Array<Any?> ->
                Log.e("chao", "peer joined " + Arrays.toString(args))
                callbackMeetingRoomListener?.onPeerJoined(args[1].toString())
            }
        } catch (e: Exception) {
            Log.d(TAG, "init: SignalingClient init ERROR MESSAGE : ${e.message}")
        }
    }

    fun meetingInitOff(){
        socket!!.off("enter")
        socket!!.off("screenShareJoined")
        socket!!.off("bye")
        socket!!.off("screenShareMessage")
        socket!!.off("shareStart")
        socket!!.off("shareStop")
        socket!!.off("drawerStart")
        socket!!.off("drawerStop")
        socket!!.off("drawerSendFromScreenShareReceiver")
        socket!!.off("roomUsersWaitUserResponse")
        socket!!.off("chatting")
        socket!!.off("message")
        socket!!.off("join")
    }

    /*방 대기하는 유저에게 오는 이벤트*/
    fun waitRoomInit(){
        socket!!.on("waitResult", onWaitResult)
    }

    fun waitRoomInitOff(){
        socket!!.off("waitResult")
    }


    /*방에 처음 입장할 경우 실행되는 메소드*/
    private val onEnter:Emitter.Listener = Emitter.Listener {
        //누군가 방에 입장시, 입장한 유저의 데이터를 받아옴
        val data = it[0]
        Log.d(TAG, "확인확인확인:  onEnter")
        if(data is JSONObject){
            Log.d(TAG, "JSONObject 확인: ")
            callbackMeetingRoomListener!!.onEnterCallback(data, null)
            callbackSubUserListListener?.onEnterCallback(data, null)
        }
        //내가 방에 입장시, 방에 입장시 유저들의 데이터를 받아옴
        else if(data is JSONArray){
            Log.d(TAG, "JSONArray 확인: ")
            callbackMeetingRoomListener!!.onEnterCallback(null, data)
            callbackSubUserListListener?.onEnterCallback(null, data)
        }
    }

    /**/
    private val onScreenShareJoined:Emitter.Listener = Emitter.Listener {
        Log.d(TAG, "확인확인확인:  onJoined")
        val socketid = it[1] as String
        presenterSocketid = it[2] as String
        callbackMeetingRoomListener!!.onScreenShareSelfJoined(presenterSocketid);
    }

    private val onBye:Emitter.Listener = Emitter.Listener {
        Log.d(TAG, "확인확인확인:  onBye")
        callbackMeetingRoomListener!!.onScreenSharePeerLeave(it[0] as JSONObject)
        callbackSubUserListListener?.onByeCallback(it[0] as JSONObject)
    }
    private val onScreenShareMessage:Emitter.Listener = Emitter.Listener {
        val it = it[0];
        if(it is String){

        }
        else if(it is JSONObject){
            val data:JSONObject = it
            when (data.optString("type")) {
                "offer" -> {
                    val to: String = data.optString("from")
                    callbackMeetingRoomListener!!.onScreenShareOfferReceived(data, to)
                }
                "answer" -> {
                    callbackMeetingRoomListener!!.onScreenShareAnswerReceived(data);
                }
                "candidate" -> {
                    callbackMeetingRoomListener!!.onScreenShareIceCandidateReceived(data);
                }
            }
        }
    }
    private val onShareStart:Emitter.Listener = Emitter.Listener {

        val user:String = it[0] as String
        if(user=="presenter"){
            callbackMeetingRoomListener!!.screenShareStart(user, null, null, null, null)
        }
        else if(user=="viewer"){
            val width:Int = it[1] as Int
            val height:Int = it[2] as Int
            val statusBarHeight:Int = it[3] as Int
            val drawerSituation:String = it[4] as String
            callbackMeetingRoomListener!!.screenShareStart(user,
                width,
                height,
                statusBarHeight,
                drawerSituation)
        }
    }
    private val onShareStop:Emitter.Listener = Emitter.Listener {
        val a:String = it[0] as String
        callbackMeetingRoomListener!!.onScreenShareStopCallback(a)
    }
    private val onDrawerStart:Emitter.Listener = Emitter.Listener {
//        val data:JSONObject = it[0] as JSONObject
        callbackMeetingRoomListener!!.onDrawerStartSocket()
    }
    private val onDrawerStop:Emitter.Listener = Emitter.Listener {
//        val data:JSONObject = it[0] as JSONObject
        callbackMeetingRoomListener!!.onDrawerStopSocket()
    }
    private val onDrawerSendFromScreenShareReceiver:Emitter.Listener = Emitter.Listener {
        val jsonArray = it[0] as JSONArray
        val color = it[1] as String
        val paintWidthToint = it[2] as Int
        val paintWidth = paintWidthToint.toFloat()
        val screenShareReceiverViewWidth = it[3] as Int
        val screenShareReceiverViewHeight = it[4] as Int
        callbackMeetingRoomListener!!.onDrawerSendFromScreenShareReceiver(jsonArray,
            color,
            paintWidth,
            screenShareReceiverViewWidth,
            screenShareReceiverViewHeight)
    }
    private val onRoomUsersWaitUserResponse:Emitter.Listener = Emitter.Listener {
        val waitUserDataToJsonObject = it[0] as JSONObject
        callbackMeetingRoomListener!!.onHostWaitUserResponse(waitUserDataToJsonObject)
        callbackSubUserListListener?.onWaitUserEnterCallback(waitUserDataToJsonObject)
    }
    /**
     * 방에 있는 유저들에게 대기자들의 결과를 보내주기
     * MeetingRoomActivity 에서는 대기자 목록을 갖고있는 어레이리스트에 데이터를 삭제해주기 위함.
     * MeetingRoomUsersList 에서는 대기자 목록에서 무조건 삭제를 목적으로 받는 이벤트이다.
     * */
    private val onRoomUsersWaitResult:Emitter.Listener = Emitter.Listener {
        val waitUserUniqueValue = it[0] as String
        callbackMeetingRoomListener?.onRoomUsersWaitResult(waitUserUniqueValue)
        callbackSubUserListListener?.onRoomUsersWaitResult(waitUserUniqueValue)
    }

    private val onWaitResult:Emitter.Listener = Emitter.Listener {

        val resultValue = it[0] as String
        callbackWaitListener?.onWaitResponse(resultValue)
    }

    private val onChatting:Emitter.Listener = Emitter.Listener {
        val chatJSONObject = it[0] as JSONObject
        callbackMeetingRoomListener?.onChatting(chatJSONObject)
    }

    /**
    * 유저가 대기방에 들어가면 방에 유저들에게 알려주기위한 메소드
    * @param userNickName 은 방에 유저들에게 누가 입장했는지 알려주기 위함
    * @param room 은 방번호
    * @param profileImgPath 은 MeetingRoomActivity에서 대기실에 있는 유저의 이미지를 보여주기 위함
    *
    * */
    fun waitUserNotifiToHost(
        userNickName: String,
        roomNum: String,
        profileImgPath: String,
        userUniqueValue: String
    ){
        val jo =JSONObject()
        try {
            jo.put("userNickName", userNickName)
            jo.put("room", roomNum)
            jo.put("profileImgPath", profileImgPath)
            jo.put("userUniqueValue", userUniqueValue)
            jo.put("from", socket!!.id())
            socket!!.emit("waitUserNotifiToRoomUsers", jo)
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    fun waitUserAcceptOrRefuse(toSocketId: String, result: String, waitUserUniqueValue: String){
        val jo = JSONObject()
        try {
            jo.put("room", roomnum)
            jo.put("result", result)
            jo.put("waitUserUniqueValue", waitUserUniqueValue)
            jo.put("to", toSocketId)
            jo.put("from", socket!!.id())
            socket!!.emit("waitResult", jo)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /*
    * 유저가 처음 어플에 들어오면 시작되는 메소드
    * 유저의 아이디로 socket을 서버에 등록을 할지
    * 유저의 유니크값으로 socket을 서버에 등록을 할지 고민
    *
    * */
    private fun userIdRegisterSocket(){
        val jo =JSONObject()
        try {
            jo.put("type", "userRegister")
//            jo.put("userId",userName)
            jo.put("userUniqueValue", userUniqueValue)
            jo.put("from", socket!!.id())
            socket!!.emit("userRegister", jo)
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    /*방을 만들면 방장의 방 기본 설정 서버로 보내기*/
    fun roomInitDataSend(autoAccept: String){
        val jo = JSONObject()
        try {
            jo.put("type", "roomInitSet")
            jo.put("room", roomnum)
            jo.put("autoAccept", autoAccept)
            socket!!.emit("roomInitSet", jo)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /*MeetingRoomActivity에 처음 들어가면 시작되는 메소드*/
    fun roomEnter(userName: String, userProfileImgPath: String, userUniqueValue: String){
        val jo =JSONObject()
        try {
            jo.put("type", "enter")
            jo.put("userName", userName)
            jo.put("userProfileImgPath", userProfileImgPath)
            jo.put("room", roomnum)
            jo.put("userUniqueValue", userUniqueValue)
            jo.put("from", socket!!.id())
            socket!!.emit("enter", jo)
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }
    fun sendIceCandidate(iceCandidate: IceCandidate, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("room", roomnum)
            jo.put("to", to)
            jo.put("from", socket!!.id())
            socket!!.emit("screenShareMessage", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //작성자: 정근영
    fun voiceSendIceCandidate(iceCandidate: IceCandidate, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("from", socket!!.id())
            jo.put("to", to)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun sendSessionDescription(sdp: SessionDescription, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("to", to)
            jo.put("room", roomnum)
            jo.put("from", socket!!.id())
            socket!!.emit("screenShareMessage", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //작성자: 정근영
    fun voiceSendSessionDescription(sdp: SessionDescription, to: String?) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("from", socket!!.id())
            jo.put("to", to)
            socket!!.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    /*화면 공유 시작*/
    fun screenShareOn(width: Int, height: Int, statusBarHeight: Int) {
        socket!!.emit("shareStart", roomnum, socket!!.id(), width, height, statusBarHeight)
    }
    /*화면 공유 멈춤*/
    fun screenShareOff(){
        val jo = JSONObject()
        try {
            jo.put("room", roomnum)
            jo.put("from", socket!!.id())
            socket!!.emit("shareStop", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun screenShareCreateAndJoin() {
        socket!!.emit("screenShare create or join", roomnum, socket!!.id())
    }
    /*방 나갈때*/
    fun exitRoom(hostAndClient: String, userUniqueValue: String){
        val jo = JSONObject()
        try {
            jo.put("room", roomnum)
            jo.put("from", socket!!.id())
            jo.put("areYouHost", hostAndClient)
            jo.put("userUniqueValue", userUniqueValue)
        } catch (e: java.lang.Exception) {
        }
        socket!!.emit("bye", jo)
//        socket!!.disconnect()
//        socket!!.close()
    }
    /*주석 작성 시작*/
    fun drawerStart(){
        val jo = JSONObject()
        try {
            jo.put("room", roomnum)
            jo.put("from", socket!!.id())
            jo.put("drawer situation", "start")
        } catch (e: java.lang.Exception) {
        }
        socket!!.emit("drawerStart", jo)
    }
    /*주석 작성 멈춤*/
    fun drawerStop(){
        val jo = JSONObject()
        try {
            jo.put("room", roomnum)
            jo.put("from", socket!!.id())
            jo.put("drawer situation", "stop")
        } catch (e: java.lang.Exception) {
        }
        socket!!.emit("drawerStop", jo)
    }

    /*주석 작성시 화면 공유받은 사람이 주석을 작성할때 화면공유한 사람에게 canvas데이터 보내주기*/
    fun drawerSendFromScreenShareReceiver(
        jsonArray: JSONArray?,
        color: String?,
        paintWidth: Float,
        screenShareReceiverViewWidth: Int,
        screenShareReceiverViewHeight: Int
    ){
        socket!!.emit("drawerSendFromScreenShareReceiver",
            jsonArray,
            color,
            paintWidth,
            roomnum,
            screenShareReceiverViewWidth,
            screenShareReceiverViewHeight)
    }

    fun remoteClick(jsonObject: JSONObject){
        socket!!.emit("remoteClick", jsonObject, roomnum)
    }

    interface CallbackSubUserListListener{
        fun onEnterCallback(jsonObject: JSONObject?, jsonArray: JSONArray?)
        fun onWaitUserEnterCallback(waitUserDataToJsonObject: JSONObject)
        fun onRoomUsersWaitResult(waitUserUniqueValue: String)
        fun onByeCallback(data: JSONObject)
    }

    interface CallbackMeetingRoomListener {
        fun onEnterCallback(jsonObject: JSONObject?, jsonArray: JSONArray?)
        fun screenShareStart(
            user: String,
            width: Int?,
            height: Int?,
            statusBarHeight: Int?,
            drawerSituation: String?
        )
        fun onScreenShareStopCallback(user: String)
        fun onScreenShareSelfJoined(presenterSocketid: String)
        fun onScreenSharePeerLeave(data: JSONObject)
        fun onScreenShareOfferReceived(data: JSONObject, to: String)
        fun onScreenShareAnswerReceived(data: JSONObject)
        fun onScreenShareIceCandidateReceived(data: JSONObject)
        fun onDrawerStartSocket()
        fun onDrawerStopSocket()
        fun onDrawerSendFromScreenShareReceiver(
            jsonArray: JSONArray?,
            color: String?,
            paintWidth: Float,
            screenShareReceiverViewWidth: Int,
            screenShareReceiverViewHeight: Int
        )
        fun onHostWaitUserResponse(waitUserDataToJsonObject: JSONObject)
        fun onRoomUsersWaitResult(waitUserUniqueValue: String)
        fun onChatting(chatJsonObject: JSONObject)
        fun onOfferReceived(data: JSONObject)   //작성자: 정근영
        fun onAnswerReceived(data: JSONObject)   //작성자: 정근영
        fun onIceCandidateReceived(data: JSONObject)   //작성자: 정근영
        fun onPeerJoined(socketId: String?)   //작성자: 정근영
    }

    interface CallbackWaitListener{
        fun onWaitResponse(resultValue: String)
    }
}