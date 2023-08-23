package com.example.ot

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.Adapter.MeetingRoomPagerFragmentAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_meetingroom_frag_share_receiver.*

/**
 *  해당 클래스는 핸들러를 상속받아 MeetingRoomActivity에서 필요한 핸들러 작업을 한다.
 *
 * @author kevin
 * @version 1.0, topView,bottomView를 onoff해주는 기능, 화면 공유 띄워주는 뷰를 초기화 해주는 기능 추가
 * @see None
 *
 * @param topView is the SelectMenulayout on top of mainlayout.
 * @param bottomView is the SelectMenulayout on bottom of mainlayout.
 * @param tabLayout 는 뷰페이저의 화면위치를 보여주는 뷰이다.(인디케이트)
 * @param screenShareView 는 공유한 사람의 화면을 띄워주는 뷰다.
 * @param eglBaseContext 는 공유한 사람의 화면을 띄워줄때 사용되는 context이다.
 */

class MeetingRoomHandler (var topView: ConstraintLayout,
                          var bottomView:LinearLayout,
                          var tabLayout:TabLayout,
                          var meetingRoomActivity: MeetingRoomActivity,
                          var viewpagerAdapter:MeetingRoomPagerFragmentAdapter) : Handler(
    Looper.getMainLooper()) {
    val TAG = "MeetingRoomHandler"

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
      if (msg.what == HandlerTypeCode.MeetingRoomAcitvityType.VIEW_CHANGE_MESSAGE) {
            meetingRoomActivity.selectMenuLayoutOnOffCk = false
            topView.visibility = View.GONE
            bottomView.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
      }
        else if(msg.what ==HandlerTypeCode.MeetingRoomAcitvityType.VIEWPAGER_ADD_PAGE_MESSAGE){
          viewpagerAdapter.notifyDataSetChanged()
          meetingRoomActivity.viewPager2_meetingroom.currentItem = meetingRoomActivity.fragmentArrayList.size-1
      }
        else if(msg.what==HandlerTypeCode.MeetingRoomAcitvityType.SCREEN_SHARE_ICON_ON_CHANGE_MESSAGE){
          meetingRoomActivity.changeShareIconText(true)
      }
      else if(msg.what==HandlerTypeCode.MeetingRoomAcitvityType.SCREEN_SHARE_ICON_OFF_CHANGE_MESSAGE){
          meetingRoomActivity.changeShareIconText(false)
      }
      else if(msg.what==HandlerTypeCode.MeetingRoomAcitvityType.USER_LIST_CHANGE_MESSAGE){
          meetingRoomActivity.usersImgMeetingRoomFragActivity!!.usersListAdapter!!.notifyDataSetChanged()
          if(meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas.visibility == View.VISIBLE){
              meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas.visibility=View.GONE
          }
      }
      else if(msg.what==HandlerTypeCode.MeetingRoomAcitvityType.SCREEN_SHARE_SURFACEVIEW_INIT){
//            meetingRoomActivity.surfaceview_meetingroom_frag_share_receiver.setMirror(false)
//            meetingRoomActivity.surfaceview_meetingroom_frag_share_receiver.init(meetingRoomActivity.eglBaseContext,null)
      }
      else if(msg.what==HandlerTypeCode.CanvasType.SCREEN_SHARE_RECEIVER_DRAWER_OPEN){
            meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas.visibility = View.VISIBLE
      }
      else if(msg.what==HandlerTypeCode.CanvasType.SCREEN_SHARE_RECEIVER_DRAWER_CLOSE) {
          meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas.visibility = View.GONE
      }
        else if(msg.what==HandlerTypeCode.CanvasType.DRAW_LINE_INVALIDATE){
          meetingRoomActivity.shareScreenShareGiverDrawerView!!.invalidate()
      }
    }
}