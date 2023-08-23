package com.example.ot.CanvasDepository

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.Resource
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.view.*
import org.webrtc.ContextUtils.getApplicationContext

class PenEraserChangeListener_DELETE_CLASS(
    var penOrEraser:String,
    var screenShareGiverDrawerView: ScreenShareGiverDrawerView?,
    var screenShareReceiverDrawerView: ScreenShareReceiverDrawerView?,
    var penOrEraserImgView:ImageView,
    var penOrEraserTextView:TextView,
    var context: MeetingRoomActivity) : View.OnClickListener {
    override fun onClick(v: View?) {
        screenShareGiverDrawerView?.penOrEraser = penOrEraser
        screenShareReceiverDrawerView?.penOrEraser = penOrEraser
        if(penOrEraser=="pen"){
            penOrEraserImgView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.meetingroom_receiver_pen_icon_white
                )
            )
            penOrEraserTextView.text = "펜"
        }
        else if(penOrEraser=="eraser"){
            penOrEraserImgView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.meetingroom_receiver_eraser_white
                )
            )
            penOrEraserTextView.text = "지우개"
        }
//        if(screenShareReceiverDrawerView!=null) {
//            context.framelayout_meetingroom_receiver_pen_toolbox.visibility = View.INVISIBLE
//        }
    }
}