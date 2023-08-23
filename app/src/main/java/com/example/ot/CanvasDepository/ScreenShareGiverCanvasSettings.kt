package com.example.ot.CanvasDepository

import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.view.*
import java.lang.Exception

class ScreenShareGiverCanvasSettings(var meetingRoomActivity: MeetingRoomActivity) {

    var TAG:String = "ScreenShareGiverCanvasSettings"

    var mWindowManager:WindowManager?=null
    var drawerStartBtn:ConstraintLayout?=null
    var drawerSketchBook:FrameLayout?=null
    fun goHome(){
        val intent = Intent(Intent.ACTION_MAIN) //태스크의 첫 액티비티로 시작
        intent.addCategory(Intent.CATEGORY_HOME) //홈화면 표시
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
        meetingRoomActivity.startActivity(intent)
    }

    fun createFloatingBtnAndSketchBook(){
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val mWindowLp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888)
        mWindowLp.gravity = Gravity.BOTTOM or Gravity.LEFT
        mWindowLp.y = 100
        mWindowLp.x = 100
        val inflater = meetingRoomActivity.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //주석 시작 버튼 생성하기
        drawerStartBtn = inflater.inflate(R.layout.drawer_home_floating_btn, null) as ConstraintLayout
        mWindowManager = meetingRoomActivity.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(drawerStartBtn, mWindowLp)
        val canvasParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888)
        //도화지 화면
        drawerSketchBook = inflater.inflate(R.layout.activity_screen_share_giver_drawer_sketchbook, null) as FrameLayout
        mWindowManager!!.addView(drawerSketchBook, canvasParams)
        drawerSketchBook!!.visibility = View.GONE

        /*도화지 생성*/
        meetingRoomActivity.shareScreenShareGiverDrawerView = drawerSketchBook!!.findViewById(R.id.view_meetingroom_giver_sketchbook)
//        meetingRoomActivity.share_MyView!!.userUniqueValue = meetingRoomActivity.userUniqueValue
//        meetingRoomActivity.share_MyView!!.callback = meetingRoomActivity
        val constlayout_canvas_cancle: ConstraintLayout = drawerSketchBook!!.findViewById(R.id.constlayout_canvas_cancle)
        /*색상 관련*/
        val share_man_select_black = drawerSketchBook!!.findViewById<LinearLayout>(R.id.select_black)
        val share_man_select_red = drawerSketchBook!!.findViewById<LinearLayout>(R.id.select_red)
        val share_man_select_yellow = drawerSketchBook!!.findViewById<LinearLayout>(R.id.select_yellow)
        val share_man_select_blue = drawerSketchBook!!.findViewById<LinearLayout>(R.id.select_blue)
        val share_linearlayout_color = drawerSketchBook!!.findViewById<LinearLayout>(R.id.linearlayout_color)
        share_man_select_black.setOnClickListener(ColorchangeListener("black", meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, share_linearlayout_color, meetingRoomActivity))
        share_man_select_red.setOnClickListener(ColorchangeListener("red", meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, share_linearlayout_color, meetingRoomActivity))
        share_man_select_yellow.setOnClickListener(ColorchangeListener("yellow", meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, share_linearlayout_color, meetingRoomActivity))
        share_man_select_blue.setOnClickListener(ColorchangeListener("blue", meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, share_linearlayout_color, meetingRoomActivity))
        /*두께 관련*/
//        val share_constlayout_select_width_25dp: ConstraintLayout = drawerSketchBook!!.findViewById(R.id.constlayout_select_width_25dp)
        val share_constlayout_select_width_25dp: ConstraintLayout = drawerSketchBook!!.constlayout_select_width_25dp
        val share_constlayout_select_width_20dp: ConstraintLayout = drawerSketchBook!!.constlayout_select_width_20dp
        val share_constlayout_select_width_15dp: ConstraintLayout = drawerSketchBook!!.constlayout_select_width_15dp
        val share_constlayout_select_width_10dp: ConstraintLayout = drawerSketchBook!!.constlayout_select_width_10dp
//        val share_linear_width_25dp:LinearLayout = drawerSketchBook!!.findViewById<LinearLayout>(R.id.linearlayout_select_width_25dp)
        val share_linear_width_25dp:LinearLayout = drawerSketchBook!!.linearlayout_select_width_25dp
        val share_linear_width_20dp:LinearLayout = drawerSketchBook!!.linearlayout_select_width_20dp
        val share_linear_width_15dp:LinearLayout = drawerSketchBook!!.linearlayout_select_width_15dp
        val share_linear_width_10dp:LinearLayout = drawerSketchBook!!.linearlayout_select_width_10dp
        share_linear_width_10dp.isSelected = true
        share_constlayout_select_width_10dp.setOnClickListener(WidthChangeListener(CanvasType.Width.SMALLEST, meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, meetingRoomActivity, share_linear_width_25dp, share_linear_width_20dp, share_linear_width_15dp, share_linear_width_10dp))
        share_constlayout_select_width_15dp.setOnClickListener(WidthChangeListener(CanvasType.Width.SMALL, meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, meetingRoomActivity, share_linear_width_25dp, share_linear_width_20dp, share_linear_width_15dp, share_linear_width_10dp))
        share_constlayout_select_width_20dp.setOnClickListener(WidthChangeListener(CanvasType.Width.MEDIUM, meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, meetingRoomActivity, share_linear_width_25dp, share_linear_width_20dp, share_linear_width_15dp, share_linear_width_10dp))
        share_constlayout_select_width_25dp.setOnClickListener(WidthChangeListener(CanvasType.Width.BIG, meetingRoomActivity.shareScreenShareGiverDrawerView!!,null, meetingRoomActivity, share_linear_width_25dp, share_linear_width_20dp, share_linear_width_15dp, share_linear_width_10dp))

        /*현재 도구 펜,지우개 선택*/
        var penImageView:ImageView = drawerSketchBook!!.imageview_meetingroom_giver_pen
        var penTextView:TextView = drawerSketchBook!!.textview_meetingroom_giver_pen
        penImageView.isSelected=true
        penTextView.isSelected=true

        var eraserImageView:ImageView = drawerSketchBook!!.imageview_meetingroom_giver_eraser
        var eraserTextView:TextView = drawerSketchBook!!.textview_meetingroom_giver_eraser
        drawerSketchBook!!.constraintlayout_meetingroom_giver_item_eraser.setOnClickListener(PenEraserChangeListener("eraser",meetingRoomActivity.shareScreenShareGiverDrawerView!!,null,penImageView,penTextView,eraserImageView,eraserTextView,meetingRoomActivity))
        drawerSketchBook!!.constraintlayout_meetingroom_giver_item_pen.setOnClickListener(PenEraserChangeListener("pen",meetingRoomActivity.shareScreenShareGiverDrawerView!!,null,penImageView,penTextView,eraserImageView,eraserTextView,meetingRoomActivity))

//        drawerSketchBook!!.linearlayout_meetingroom_giver_select_eraser.setOnClickListener(PenEraserChangeListener("eraser",meetingRoomActivity.shareScreenShareGiverDrawerView!!,null,imageViewPenOrEraser,textViewPenOrEraser,meetingRoomActivity))
//        drawerSketchBook!!.linearlayout_meetingroom_giver_select_pen.setOnClickListener(PenEraserChangeListener("pen",meetingRoomActivity.shareScreenShareGiverDrawerView!!,null,imageViewPenOrEraser,textViewPenOrEraser,meetingRoomActivity))

        /*지우기 기능*/
        var eraserAllBtn:ConstraintLayout =  drawerSketchBook!!.constraintlayout_meetingroom_receiver_item_remove
        eraserAllBtn.setOnClickListener {
            meetingRoomActivity.runOnUiThread {
                meetingRoomActivity.shareScreenShareGiverDrawerView!!.mbitmap!!.eraseColor(Color.TRANSPARENT)
                meetingRoomActivity.shareScreenShareGiverDrawerView!!.invalidate()
            }
        }
        //상대방 드로워 화면 숨기기
        constlayout_canvas_cancle.setOnClickListener {
            drawerStartBtn!!.visibility = View.VISIBLE
            drawerSketchBook!!.visibility = View.GONE
            meetingRoomActivity.onDrawerStop()
        }

        //상대방 드로워 화면 띄워주기
        drawerStartBtn!!.setOnClickListener {
            drawerStartBtn!!.visibility = View.GONE
            drawerSketchBook!!.visibility = View.VISIBLE
            meetingRoomActivity.onDrawerStart()
        }


        /*캔버스 도구들 gone,visible 시켜주는 곳*/
//        val drawerItemPenBtn = drawerSketchBook!!.constraintlayout_meetingroom_giver_item_pen
        val drawerItemColorBtn = drawerSketchBook!!.constraintlayout_meetingroom_giver_item_color
        val drawerItemWidthBtn = drawerSketchBook!!.constraintlayout_meetingroom_giver_item_width

//        val drawerItemBoxPen = drawerSketchBook!!.framelayout_meetingroom_giver_pen_toolbox
        val drawerItemBoxColor = drawerSketchBook!!.framelayout_meetingroom_giver_color_toolbox
        val drawerItemBoxWidth = drawerSketchBook!!.framelayout_meetingroom_giver_width_toolbox
//        drawerItemPenBtn.setOnClickListener {
////            if(drawerItemBoxPen.visibility==View.VISIBLE){
////                drawerItemBoxPen.visibility = View.INVISIBLE
////            }
////            else  if(drawerItemBoxPen.visibility==View.INVISIBLE){
////                drawerItemBoxPen.visibility = View.VISIBLE
////            }
//            drawerItemBoxColor.visibility=View.INVISIBLE
//            drawerItemBoxWidth.visibility=View.INVISIBLE
//        }

        drawerItemColorBtn.setOnClickListener {
            if(drawerItemBoxColor.visibility==View.VISIBLE){
                drawerItemBoxColor.visibility = View.INVISIBLE
            }
            else  if(drawerItemBoxColor.visibility==View.INVISIBLE){
                drawerItemBoxColor.visibility = View.VISIBLE
            }
//            drawerItemBoxPen.visibility=View.INVISIBLE
            drawerItemBoxWidth.visibility=View.INVISIBLE
        }
        drawerItemWidthBtn.setOnClickListener {
            if(drawerItemBoxWidth.visibility==View.VISIBLE){
                drawerItemBoxWidth.visibility = View.INVISIBLE
            }
            else  if(drawerItemBoxWidth.visibility==View.INVISIBLE){
                drawerItemBoxWidth.visibility = View.VISIBLE
            }
            drawerItemBoxColor.visibility=View.INVISIBLE
//            drawerItemBoxPen.visibility=View.INVISIBLE
        }

        meetingRoomActivity.shareScreenShareGiverDrawerView!!.setOnClickListener {
            drawerItemWidthBtn.visibility = View.INVISIBLE
            drawerItemBoxColor.visibility=View.INVISIBLE
//            drawerItemBoxPen.visibility=View.INVISIBLE
        }

    }

    fun removeFloatingBtnAndSketchBook(){
        try {
            mWindowManager?.removeView(drawerStartBtn)
            mWindowManager?.removeView(drawerSketchBook)
        }catch (e:Exception){
            Log.d(TAG, "removeFloatingBtnAndSketchBook: 에러 확인 : "+e.toString())
        }
    }

}