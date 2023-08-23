package com.example.canvassendtoyproject

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.example.ot.CanvasDepository.ScreenShareGiverDrawerView
import com.example.ot.HandlerTypeCode

class CanvasHandler(var screenShareGiverDrawerView: ScreenShareGiverDrawerView, var screen_share_btn: Button, var frameLayout_draw_canvas: FrameLayout) : Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (msg.what == HandlerTypeCode.CanvasType.DRAW_LINE_INVALIDATE) {
            screenShareGiverDrawerView.invalidate()
        } else if (msg.what == HandlerTypeCode.CanvasType.SCREEN_SHARE_BTN_VISIBLE_GONE) {
            screen_share_btn.visibility = View.GONE
            frameLayout_draw_canvas.visibility = View.VISIBLE
        }
    }
}
