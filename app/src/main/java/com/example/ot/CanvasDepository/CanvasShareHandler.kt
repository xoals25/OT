package com.example.canvassendtoyproject

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.ot.CanvasDepository.ScreenShareGiverDrawerView
import com.example.ot.HandlerTypeCode

class CanvasShareHandler(var screenShareGiverDrawerView: ScreenShareGiverDrawerView) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (msg.what ==HandlerTypeCode.CanvasType.DRAW_LINE_INVALIDATE) {
            screenShareGiverDrawerView.invalidate()
        }
    }
}