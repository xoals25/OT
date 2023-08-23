package com.example.ot.CanvasDepository

import android.view.View
import android.widget.LinearLayout
import com.example.ot.Activity.MeetingRoomActivity
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_screen_share_giver_drawer_sketchbook.*

class WidthChangeListener(var width: Float,
                          var screenShareGiverDrawerView: ScreenShareGiverDrawerView?,
                          var screenShareReceiverDrawerView: ScreenShareReceiverDrawerView?,
                          var context: MeetingRoomActivity,
                          var linear_width_25dp: LinearLayout,
                          var linear_width_20dp: LinearLayout,
                          var linear_width_15dp: LinearLayout,
                          var linear_width_10dp: LinearLayout) : View.OnClickListener {
    override fun onClick(v: View) {
        screenShareGiverDrawerView?.settingWidth(width)
        screenShareReceiverDrawerView?.settingPaintWidth(width)
        when (width) {
            CanvasType.Width.SMALLEST -> {
                changeSizeColor(false, false, false, true)
            }
            CanvasType.Width.SMALL -> {
                changeSizeColor(false, false, true, false)
            }
            CanvasType.Width.MEDIUM -> {
                changeSizeColor(false, true, false, false)
            }
            CanvasType.Width.BIG -> {
                changeSizeColor(true, false, false, false)
            }
        }
        if(screenShareReceiverDrawerView!=null) {
            context.framelayout_meetingroom_receiver_width_toolbox.visibility = View.INVISIBLE
        }
    }

    private fun changeSizeColor(w25: Boolean, w20: Boolean, w15: Boolean, w10: Boolean) {
        linear_width_25dp.isSelected = w25
        linear_width_20dp.isSelected = w20
        linear_width_15dp.isSelected = w15
        linear_width_10dp.isSelected = w10
    }
}