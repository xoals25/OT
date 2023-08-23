package com.example.ot.CanvasDepository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.Socket.MeetingRoomSignalingClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ScreenShareReceiverDrawerView : View {
    var TAG = "MyView"
    var paint = Paint()
    var eraserPaint = Paint()
    var canvas: Canvas? = null
    var mbitmap: Bitmap? = null
    var oldX:Float = 0.0F
    var oldY:Float = -1.0F

    /*유저 정보 관련*/
    var userUniqueValue:String? = null
    /*색상설정*/
    var color = "black"

    /*펜이냐 지우개냐 설정*/
    var penOrEraser = "pen"

    /*굵기 설정*/
    var paintWidth: Float = CanvasType.Width.SMALLEST
    var jsonArray: JSONArray? = null
    var jsonObject: JSONObject? = null

    /*상대방 화면에 맞춰서 정해진 view의 크기*/
    var screenShareReceiverViewWidth:Int?=0
    var screenShareReceiverViewHeight:Int?=0

    var callBackListener:CallBackListener?=null

    constructor(context: Context) : super(context, null){common()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0){common()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){common()}

    fun settingColor(color: String) {
        this.color = color
        when (color) {
            "black" -> {
                paint.color = Color.BLACK
            }
            "red" -> {
                paint.color = Color.RED
            }
            "yellow" -> {
                paint.color = Color.YELLOW
            }
            "blue" -> {
                paint.color = Color.parseColor("#00a8f3")
            }
        }
    }

    fun settingPaintWidth(paintWidth: Float) {
        this.paintWidth = paintWidth
        paint.strokeWidth = paintWidth
        eraserPaint.strokeWidth = paintWidth
    }
    fun common() {
        paint.style = Paint.Style.STROKE // 선이 그려지도록
        paint.strokeWidth = paintWidth // 선의 굵기 지정
        paint.strokeCap = Paint.Cap.ROUND //이걸 하지 않으면 선이 끊기는 현상 발생한다. //이유는 ROUND는 O으로 시작해서 이어나가는데 ROUND를 하지않으면 -들이 이어지기 때문에 매끄럽지 못하다
        paint.isAntiAlias = true

        eraserPaint.style = Paint.Style.STROKE
        eraserPaint.strokeWidth = paintWidth
        eraserPaint.strokeCap = Paint.Cap.ROUND
        eraserPaint.isAntiAlias = true
        eraserPaint.color = Color.LTGRAY
//        paint.color = Color.TRANSPARENT
//        paint.color = Color.BLACK
        mbitmap = Bitmap.createBitmap(2000, 5000, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mbitmap!!)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(mbitmap!!, 0f, 0f, paint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val X:Float = event!!.x
        val Y:Float = event!!.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                oldX = X
                oldY = Y
                jsonArray = JSONArray()
                jsonObject = JSONObject()
                if (penOrEraser == "pen") {
                    callBackListener!!.removeCancleDrawer()
                    canvas!!.drawLine(oldX, oldY, X, Y, paint)
                } else if (penOrEraser == "eraser") {
                    mbitmap!!.eraseColor(Color.TRANSPARENT)
                    canvas!!.drawLine(oldX, oldY, X, Y, eraserPaint)
                }
                try {
//                    jsonObject!!.put("userid", userUniqueValue!!)
                    jsonObject!!.put("X", X)
                    jsonObject!!.put("Y", Y)
                    jsonArray!!.put(jsonObject)
                } catch (e: JSONException) {
                }
                invalidate() // 화면을 다시그려라
            }
            MotionEvent.ACTION_MOVE -> {
                if (penOrEraser == "pen") {
                    canvas!!.drawLine(oldX, oldY, X, Y, paint)
                } else if (penOrEraser == "eraser") {
                    mbitmap!!.eraseColor(Color.TRANSPARENT)
                    canvas!!.drawLine(oldX, oldY, X, Y, eraserPaint)
                }
                oldX = X
                oldY = Y
                jsonObject = JSONObject()
                try {
//                    jsonObject!!.put("userid", userUniqueValue!!)
                    jsonObject!!.put("X", X)
                    jsonObject!!.put("Y", Y)
                    jsonArray!!.put(jsonObject)
                } catch (e: JSONException) {
                    Log.d(TAG, "onTouchEvent: ERROR MESSAGE for onTouchEvent :$e")
                }
                invalidate() // 화면을 다시그려라
            }
            MotionEvent.ACTION_UP -> {
                jsonObject = JSONObject()
                canvas!!.drawLine(oldX, oldY, X, Y, paint)
                try {
//                    jsonObject!!.put("userid", userUniqueValue!!)
                    jsonObject!!.put("X", X)
                    jsonObject!!.put("Y", Y)
                    jsonArray!!.put(jsonObject)
                } catch (e: JSONException) {
                    Log.d(TAG, "onTouchEvent: ERROR MESSAGE for onTouchEvent :$e")
                }
                if (penOrEraser == "pen") {
                    callBackListener!!.drawerSendFromScreenShareReceiver(jsonArray!!,
                        color,
                        paintWidth,
                        screenShareReceiverViewWidth!!,
                        screenShareReceiverViewHeight!!)
                    callBackListener!!.removeDrawer(mbitmap!!)

                } else if (penOrEraser == "eraser") {
                    callBackListener!!.drawerSendFromScreenShareReceiver(jsonArray!!,
                        "transparente",
                        paintWidth,
                        screenShareReceiverViewWidth!!,
                        screenShareReceiverViewHeight!!)
                    mbitmap!!.eraseColor(Color.TRANSPARENT)
                }
                invalidate() // 화면을 다시그려라
            }
        }
        return true
    }

    fun erase() {
        /*지우는 방법 1*/
        mbitmap = Bitmap.createBitmap(2000, 5000, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mbitmap!!)
        //        canvas.drawBitmap(mbitmap,0,0,paint);
        invalidate() // 화면을 다시그려라
        /*지우는 방법 2*/
//        mbitmap!!.eraseColor(Color.TRANSPARENT);
    }

    interface CallBackListener {
        fun drawerSendFromScreenShareReceiver(jsonArray: JSONArray,color:String,paintWidth:Float,screenShareReceiverViewWidth:Int,screenShareReceiverViewHeight:Int)
        fun removeDrawer(bitmap:Bitmap)
        fun removeCancleDrawer()
    }
}