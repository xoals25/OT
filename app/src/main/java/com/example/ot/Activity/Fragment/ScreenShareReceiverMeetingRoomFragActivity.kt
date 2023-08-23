package com.example.ot.Activity.Fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.CanvasDepository.ScreenShareReceiverDrawerView
import com.example.ot.CanvasDepository.SurfaceViewRendererCustom
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_meetingroom_frag_share_receiver.*
import kotlinx.android.synthetic.main.activity_meetingroom_frag_share_receiver.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer
import java.lang.Exception


/**
 * 누군가 핸드폰화면 공유를 한다면 뷰페이저에 추가되어 공유한 화면을 볼 수 있는 클래스이다.
 *
 * @author kevin
 * @version 1.0, 공유받은 상대화면 띄워주기
 * @see None

 */
class ScreenShareReceiverMeetingRoomFragActivity(
    var meetingRoomActivity: MeetingRoomActivity,
    var eglBaseContext: EglBase.Context,
    var shareWidth: Int,
    var shareHeight: Int,
    var shareStatusBarHeight: Int
) :Fragment(),ScreenShareReceiverDrawerView.CallBackListener {
    var TAG:String = "ScreenShareReceiverMeetingRoomFragActivity"
    var receiverScreenShareView: SurfaceViewRenderer? = null
//    var receiverScreenShareView: SurfaceViewRendererCustom? = null
    var drawerView: ScreenShareReceiverDrawerView?=null
    var handler:Handler= Handler(Looper.getMainLooper())
    var runnable:Runnable?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:View = inflater.inflate(R.layout.activity_meetingroom_frag_share_receiver,
            container,
            false)
        v.setOnClickListener {
            meetingRoomActivity.setSelectMenuLayoutOnOff()
        }
        receiverScreenShareView = v.findViewById(R.id.surfaceview_meetingroom_frag_share_receiver)
        receiverScreenShareView!!.setMirror(false)
        receiverScreenShareView!!.init(eglBaseContext, null)
//        receiverScreenShareView!!.setMeetingRoomActivity(meetingRoomActivity)
        drawerView=v.findViewById(R.id.view_meetingroom_receiver_sketchbook)
        drawerView!!.callBackListener = this

        meetingRoomActivity.meetingRoomSignalingClient!!.screenShareCreateAndJoin()

        /*화면 공유 받는 뷰의 크기 알아내기*/
        receiverScreenShareView!!.post(Runnable {
            val width: Int = receiverScreenShareView!!.width
            val height: Int = receiverScreenShareView!!.height
            /*receiverScreenShareView의 width값(drawer width) 구하는 공식 drawerViewNewWidth
             (receiverScreenShareView의 heigth * 상대방 화면 width) / 상대방 heigth */
            var drawerViewNewWidth: Int = (height * shareWidth) / shareHeight
            Log.d(TAG, "onCreateView: drawerViewNewWidth : $drawerViewNewWidth")
            Log.d(TAG, "onCreateView: shareWidth : $shareWidth")
            var drawerViewStatusBarHeight: Int =(shareStatusBarHeight * drawerViewNewWidth) / shareWidth
//            var drawerViewStatusBarHeight: Int = if (drawerViewNewWidth > shareWidth) {
//                Log.d(TAG, "onCreateView: 확인1")
//
//                (shareStatusBarHeight * drawerViewNewWidth) / shareWidth
//
//            } else {
//                Log.d(TAG, "onCreateView: 확인2")
//                (shareStatusBarHeight * shareWidth) / drawerViewNewWidth
//            }

            drawerView!!.setWidth(drawerViewNewWidth)
            drawerView!!.setHeight(receiverScreenShareView!!.height - drawerViewStatusBarHeight)
            drawerView!!.screenShareReceiverViewHeight = receiverScreenShareView!!.height
            drawerView!!.screenShareReceiverViewWidth = drawerViewNewWidth
        })

        return v
    }


    override fun onResume() {
        super.onResume()
        //
        if(meetingRoomActivity.drawerSituation=="start") {
            meetingRoomActivity.drawerStartSetting()
        }
    }
    fun View.setWidth(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.width = value
            layoutParams = lp
        }
    }
    fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    override fun drawerSendFromScreenShareReceiver(
        jsonArray: JSONArray,
        color: String,
        paintWidth: Float,
        screenShareReceiverViewWidth: Int,
        screenShareReceiverViewHeight: Int
    ) {
       meetingRoomActivity.meetingRoomSignalingClient!!.drawerSendFromScreenShareReceiver(jsonArray,
           color,
           paintWidth,
           screenShareReceiverViewWidth,
           screenShareReceiverViewHeight)

    }

    override fun removeDrawer(bitmap: Bitmap) {
        runnable = Runnable {
            bitmap.eraseColor(Color.TRANSPARENT)
            drawerView!!.invalidate() }
        handler.postDelayed(runnable!!, 1500)
    }

    override fun removeCancleDrawer() {
        if(runnable!=null) {
            Log.d("TAG", "removeCancleDrawer: 확인")
            handler.removeCallbacks(runnable!!)
        }
    }
}