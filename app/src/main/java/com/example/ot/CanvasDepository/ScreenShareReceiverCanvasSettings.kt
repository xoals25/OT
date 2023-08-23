package com.example.ot.CanvasDepository

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.ot.Activity.MeetingRoomActivity
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_meetingroom_frag_share_receiver.*

class ScreenShareReceiverCanvasSettings(var meetingRoomActivity: MeetingRoomActivity ) {

    var TAG = "ScreenShareReceiverCanvasSettings"

    fun createSketchBook(){
        //view_meetingroom_receiver_sketchbook의 크기도 조절 해주자 (상대 화면에 따라서)

//        var drawerView:ScreenShareReceiverDrawerView = view_meetingroom_receiver_sketchbook
//        var drawerView:ScreenShareReceiverDrawerView = meetingRoomActivity.findViewById(R.id.view_meetingroom_receiver_sketchbook)
        var drawerView:ScreenShareReceiverDrawerView = meetingRoomActivity.screenSHareReceiverActivity!!.drawerView!!
//        var drawerViewFrameLayout:FrameLayout = meetingRoomActivity.findViewById(R.id.frameLayout_meetingroom_receiver_draw_canvas)
        var drawerViewFrameLayout:FrameLayout = meetingRoomActivity.screenSHareReceiverActivity!!.frameLayout_meetingroom_receiver_draw_canvas
//        var drawerView:ScreenShareReceiverDrawerView = meetingRoomActivity.view_meetingroom_receiver_sketchbook
//        var drawerViewFrameLayout:FrameLayout = meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas
        var paintColorLinearLayout:LinearLayout = meetingRoomActivity.linearlayout_color
        //뷰 최상단으로 나오게하기
//        meetingRoomActivity.runOnUiThread {
//            meetingRoomActivity.frameLayout_meetingroom_receiver_draw_canvas.bringToFront()
//            meetingRoomActivity.test.bringToFront()
//        }
        /*현재 도구 색상 선택*/
        meetingRoomActivity.select_black.setOnClickListener(ColorchangeListener("black",null, drawerView!!, paintColorLinearLayout!!, meetingRoomActivity))
        meetingRoomActivity.select_red.setOnClickListener(ColorchangeListener("red",null, drawerView!!, paintColorLinearLayout!!, meetingRoomActivity))
        meetingRoomActivity.select_yellow.setOnClickListener(ColorchangeListener("yellow",null, drawerView!!, paintColorLinearLayout!!, meetingRoomActivity))
        meetingRoomActivity.select_blue.setOnClickListener(ColorchangeListener("blue",null, drawerView!!, paintColorLinearLayout!!, meetingRoomActivity))

        /*현재 도구 펜,지우개 선택*/
//        var imageViewPenOrEraser = meetingRoomActivity.imageView_meetingroom_receiver_pen_or_eraser
//        var textViewPenOrEraser = meetingRoomActivity.textview_meetingroom_receiver_pen_or_eraser
//        meetingRoomActivity.linearlayout_meetingroom_select_eraser.setOnClickListener(PenEraserChangeListener("eraser",null,drawerView!!,imageViewPenOrEraser,textViewPenOrEraser,meetingRoomActivity))
//        meetingRoomActivity.linearlayout_meetingroom_select_pen.setOnClickListener(PenEraserChangeListener("pen",null,drawerView!!,imageViewPenOrEraser,textViewPenOrEraser,meetingRoomActivity))
        var penImageView = meetingRoomActivity.imageView_meetingroom_receiver_pen
        var penTextView = meetingRoomActivity.textview_meetingroom_receiver_pen
        var eraserImageView = meetingRoomActivity.imageView_meetingroom_receiver_eraser
        var eraserTextView = meetingRoomActivity.textview_meetingroom_receiver_eraser
        if(!eraserImageView.isSelected){
            penImageView.isSelected=true
            penTextView.isSelected=true
        }
        meetingRoomActivity.constraintlayout_meetingroom_receiver_item_pen.setOnClickListener(PenEraserChangeListener("pen",null,drawerView!!,penImageView,penTextView,eraserImageView,eraserTextView,meetingRoomActivity))
        meetingRoomActivity.constraintlayout_meetingroom_receiver_item_eraser .setOnClickListener(PenEraserChangeListener("eraser",null,drawerView!!,penImageView,penTextView,eraserImageView,eraserTextView,meetingRoomActivity))
        /*도구 굵기 선택*/
        var paintWidth25dp:LinearLayout = meetingRoomActivity.linearlayout_select_width_25dp
        var paintWidth20dp:LinearLayout = meetingRoomActivity.linearlayout_select_width_20dp
        var paintWidth15dp:LinearLayout = meetingRoomActivity.linearlayout_select_width_15dp
        var paintWidth10dp:LinearLayout = meetingRoomActivity.linearlayout_select_width_10dp
        paintWidth10dp.isSelected = true
        meetingRoomActivity.constlayout_select_width_10dp.setOnClickListener(WidthChangeListener(CanvasType.Width.SMALLEST, null,drawerView!!, meetingRoomActivity, paintWidth25dp!!, paintWidth20dp!!, paintWidth15dp!!, paintWidth10dp!!))
        meetingRoomActivity.constlayout_select_width_15dp.setOnClickListener(WidthChangeListener(CanvasType.Width.SMALL, null,drawerView!!, meetingRoomActivity, paintWidth25dp!!, paintWidth20dp!!, paintWidth15dp!!, paintWidth10dp!!))
        meetingRoomActivity.constlayout_select_width_20dp.setOnClickListener(WidthChangeListener(CanvasType.Width.MEDIUM, null,drawerView!!, meetingRoomActivity, paintWidth25dp!!, paintWidth20dp!!, paintWidth15dp!!, paintWidth10dp!!))
        meetingRoomActivity.constlayout_select_width_25dp.setOnClickListener(WidthChangeListener(CanvasType.Width.BIG, null,drawerView!!, meetingRoomActivity, paintWidth25dp!!, paintWidth20dp!!, paintWidth15dp!!, paintWidth10dp!!))

        /*화면 gone visible 시켜주는 곳*/
        var canvasStartBtnConstLayout = meetingRoomActivity.constlayout_meetingroom_receiver_canvas_start_btn
        var canvasStopBtnConstLayout = meetingRoomActivity.constlayout_meetingroom_receiver_canvas_stop_btn
        var canvasDrawerItemsLinearLayout = meetingRoomActivity.meetingroom_receiver_canvas_drawer_item

//        var drawerItemPenBtn = meetingRoomActivity.constraintlayout_meetingroom_receiver_item_pen_or_eraser
        var drawerItemColorBtn = meetingRoomActivity.constraintlayout_meetingroom_receiver_item_color
        var drawerItemWidthBtn = meetingRoomActivity.constraintlayout_meetingroom_receiver_item_width

//        var drawerItemBoxPen = meetingRoomActivity.framelayout_meetingroom_receiver_pen_toolbox
        var drawerItemBoxColor = meetingRoomActivity.framelayout_meetingroom_receiver_color_toolbox
        var drawerItemBoxWidth = meetingRoomActivity.framelayout_meetingroom_receiver_width_toolbox

        canvasStartBtnConstLayout.setOnClickListener {
            meetingRoomActivity.runOnUiThread{
                canvasStartBtnConstLayout.visibility = View.GONE
                canvasDrawerItemsLinearLayout.visibility = View.VISIBLE //주석 아이템들
                drawerViewFrameLayout.visibility = View.VISIBLE
                meetingRoomActivity.constlayout_meetingroom_bottom_view.visibility = View.GONE
                meetingRoomActivity.constlayout_meetingroom_top_view.visibility = View.GONE
                //뷰페이저 스와이프 막아주기 ->안그러면 오른쪽왼쪽으로 주석 작성시 canvas touch event가 먹히지 않고 viewpager가 이벤트를 가져가서 drawer가 먹히지 않음
                //혹시 false인 상태로 상대가 주석 작성을 멈추면 뷰페이저가 계속 false이니까 meetingRoomActivity의 onDrawerStopSocket콜백함수에 true해주자.
                meetingRoomActivity.viewPager2_meetingroom.isUserInputEnabled=false
            }
        }

        canvasStopBtnConstLayout.setOnClickListener {
            meetingRoomActivity.runOnUiThread {
                canvasStartBtnConstLayout.visibility = View.VISIBLE
                canvasDrawerItemsLinearLayout.visibility = View.GONE
                drawerViewFrameLayout.visibility = View.GONE
                meetingRoomActivity.constlayout_meetingroom_bottom_view.visibility = View.VISIBLE
                meetingRoomActivity.constlayout_meetingroom_top_view.visibility = View.VISIBLE
                drawerItemBoxWidth.visibility = View.INVISIBLE
                drawerItemBoxColor.visibility = View.INVISIBLE
//                drawerItemBoxPen.visibility = View.INVISIBLE
                //뷰페이저 풀어주기
                meetingRoomActivity.viewPager2_meetingroom.isUserInputEnabled=true
            }
        }


//        drawerItemPenBtn.setOnClickListener {
//            meetingRoomActivity.runOnUiThread {
//                if (drawerItemBoxPen.visibility == View.VISIBLE) {
//                    drawerItemBoxPen.visibility = View.INVISIBLE
//                } else if (drawerItemBoxPen.visibility == View.INVISIBLE) {
//                    drawerItemBoxPen.visibility = View.VISIBLE
//                }
//                drawerItemBoxColor.visibility = View.INVISIBLE
//                drawerItemBoxWidth.visibility = View.INVISIBLE
//            }
//        }

        drawerItemColorBtn.setOnClickListener {
            meetingRoomActivity.runOnUiThread {
                if (drawerItemBoxColor.visibility == View.VISIBLE) {
                    drawerItemBoxColor.visibility = View.INVISIBLE
                } else if (drawerItemBoxColor.visibility == View.INVISIBLE) {
                    drawerItemBoxColor.visibility = View.VISIBLE
                }
//                drawerItemBoxPen.visibility = View.INVISIBLE
                drawerItemBoxWidth.visibility = View.INVISIBLE
            }
        }
        drawerItemWidthBtn.setOnClickListener {
            meetingRoomActivity.runOnUiThread {
                if (drawerItemBoxWidth.visibility == View.VISIBLE) {
                    drawerItemBoxWidth.visibility = View.INVISIBLE
                } else if (drawerItemBoxWidth.visibility == View.INVISIBLE) {
                    drawerItemBoxWidth.visibility = View.VISIBLE
                }
                drawerItemBoxColor.visibility = View.INVISIBLE
//                drawerItemBoxPen.visibility = View.INVISIBLE
            }
        }

    }
}