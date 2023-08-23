package com.example.ot.CanvasDepository

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.*

class ColorchangeListener(var color: String, var screenShareGiverDrawerView: ScreenShareGiverDrawerView?,var screenShareReceiverDrawerView: ScreenShareReceiverDrawerView?, var linearlayout_color: LinearLayout, var context: MeetingRoomActivity) : View.OnClickListener {
    override fun onClick(v: View) {
        screenShareGiverDrawerView?.settingColor(color)
        screenShareReceiverDrawerView?.settingColor(color)
        if(screenShareReceiverDrawerView==null){
        }
        else{
        }
        when (color) {
            "black" -> {
                linearlayout_color.setBackgroundResource(
                    R.drawable.meetingroom_receiver_drawer_color_oval_black
                )
            }
            "red" -> {
                linearlayout_color.setBackgroundResource(
                    R.drawable.meetingroom_receiver_drawer_color_oval_red
                )
            }
            "yellow" -> {
                linearlayout_color.setBackgroundResource(
                    R.drawable.meetingroom_receiver_drawer_color_oval_yellow
                )
            }
            "blue" -> {
                linearlayout_color.setBackgroundResource(
                    R.drawable.meetingroom_receiver_drawer_color_oval_blue
                )
            }
        }
        if(screenShareReceiverDrawerView!=null) {
            context.framelayout_meetingroom_receiver_color_toolbox.visibility = View.INVISIBLE
        }
    }
}
