package com.example.ot.CanvasDepository

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ScreenShareGiverDrawerView : View {
    var TAG = "MyView"
    var paint = Paint()
    var friend_paint = Paint()
    var eraserPaint = Paint()
    var eraser = Paint()
    var canvas: Canvas? = null
    var eraserCanvas: Canvas? = null
    var mbitmap: Bitmap? = null
    var eraserBitmap: Bitmap? = null
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

    fun settingWidth(paintWidth: Float) {
        this.paintWidth = paintWidth
        paint.strokeWidth = paintWidth
        eraserPaint.strokeWidth = paintWidth
        eraser.strokeWidth = paintWidth
    }
    fun common() {
        paint.style = Paint.Style.STROKE // 선이 그려지도록
        paint.strokeWidth = paintWidth // 선의 굵기 지정
        paint.strokeCap = Paint.Cap.ROUND //이걸 하지 않으면 선이 끊기는 현상 발생한다. //이유는 ROUND는 O으로 시작해서 이어나가는데 ROUND를 하지않으면 -들이 이어지기 때문에 매끄럽지 못하다
        paint.isAntiAlias = true
//        paint.color = Color.RED
        friend_paint.style = Paint.Style.STROKE // 선이 그려지도록
        friend_paint.strokeWidth = paintWidth // 선의 굵기 지정
        friend_paint.strokeCap = Paint.Cap.ROUND //이걸 하지 않으면 선이 끊기는 현상 발생한다. //이유는 ROUND는 O으로 시작해서 이어나가는데 ROUND를 하지않으면 -들이 이어지기 때문에 매끄럽지 못하다
        friend_paint.isAntiAlias = true

        eraserPaint.style = Paint.Style.STROKE
        eraserPaint.strokeWidth = paintWidth
        eraserPaint.strokeCap = Paint.Cap.ROUND
        eraserPaint.isAntiAlias = true
        eraserPaint.color = Color.LTGRAY

        eraser.style = Paint.Style.STROKE
        eraser.strokeWidth = paintWidth
        eraser.strokeCap = Paint.Cap.ROUND
        eraser.isAntiAlias = true
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        mbitmap = Bitmap.createBitmap(2000, 5000, Bitmap.Config.ARGB_8888)
        eraserBitmap = Bitmap.createBitmap(2000, 5000, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mbitmap!!)
        eraserCanvas = Canvas(eraserBitmap!!)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(mbitmap!!, 0f, 0f, paint)
        canvas!!.drawBitmap(eraserBitmap!!, 0f, 0f, eraserPaint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val X:Float = event!!.x
        val Y:Float = event!!.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = X
                oldY = Y
                if (penOrEraser == "pen") {
                    canvas!!.drawLine(oldX, oldY, X, Y, paint)
                } else if (penOrEraser == "eraser") {
                    eraserBitmap!!.eraseColor(Color.TRANSPARENT)
                    eraserCanvas!!.drawLine(oldX, oldY, X, Y, eraserPaint)
                    canvas!!.drawLine(oldX, oldY, X, Y, eraser)
                }
                invalidate() // 화면을 다시그려라
            }
            MotionEvent.ACTION_MOVE -> {
                if (penOrEraser == "pen") {
                    canvas!!.drawLine(oldX, oldY, X, Y, paint)
                } else if (penOrEraser == "eraser") {
                    eraserBitmap!!.eraseColor(Color.TRANSPARENT)
                    eraserCanvas!!.drawLine(oldX, oldY, X, Y, eraserPaint)
                    canvas!!.drawLine(oldX, oldY, X, Y, eraser)
                }
                oldX = X
                oldY = Y
                invalidate() // 화면을 다시그려라
            }
            MotionEvent.ACTION_UP -> {
                jsonObject = JSONObject()
                if (penOrEraser == "pen") {
                } else if (penOrEraser == "eraser") {
                    eraserCanvas!!.drawLine(oldX, oldY, X, Y, eraserPaint)
                    canvas!!.drawLine(oldX, oldY, X, Y, eraser)
                    eraserBitmap!!.eraseColor(Color.TRANSPARENT)
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
//        mbitmap.eraseColor(Color.TRANSPARENT);
    }
}