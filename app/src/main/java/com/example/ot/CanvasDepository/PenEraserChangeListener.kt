package com.example.ot.CanvasDepository

import android.content.Context
import android.util.Log
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

class PenEraserChangeListener(
    var penOrEraser:String,
    var screenShareGiverDrawerView: ScreenShareGiverDrawerView?,
    var screenShareReceiverDrawerView: ScreenShareReceiverDrawerView?,
    var penImageView:ImageView,
    var penTextView: TextView,
    var eraserImageView:ImageView,
    var eraserTextView: TextView,
    var context: MeetingRoomActivity) : View.OnClickListener {
    override fun onClick(v: View?) {
        Log.d("TAG", "onClick: 확인작업버버법")
        screenShareGiverDrawerView?.penOrEraser = penOrEraser
        screenShareReceiverDrawerView?.penOrEraser = penOrEraser
        if(penOrEraser=="pen"){
            penImageView.isSelected = true
            penTextView.isSelected = true
            eraserImageView.isSelected = false
            eraserTextView.isSelected = false
        }
        else if(penOrEraser=="eraser"){
            penImageView.isSelected = false
            penTextView.isSelected = false
            eraserImageView.isSelected = true
            eraserTextView.isSelected = true
        }
    }
}