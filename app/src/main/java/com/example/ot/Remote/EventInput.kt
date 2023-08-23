package com.example.ot.Remote

import android.hardware.input.InputManager
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * Created by omerjerk on 19/9/15.
 *
 * Class to create seamless input/touch events on your Android device without root
 */
class EventInput {
    var injectInputEventMethod: Method
    var im: InputManager

    init {
        //Get the instance of InputManager class using reflection
        var methodName = "getInstance"
        val objArr = arrayOfNulls<Any>(0)
        im = InputManager::class.java.getDeclaredMethod(methodName, *arrayOfNulls(0))
            .invoke(null, *objArr) as InputManager

        //Make MotionEvent.obtain() method accessible
        methodName = "obtain"
        MotionEvent::class.java.getDeclaredMethod(methodName, *arrayOfNulls(0)).isAccessible =
            true

        //Get the reference to injectInputEvent method
        methodName = "injectInputEvent"
        injectInputEventMethod = InputManager::class.java.getMethod(
            methodName, *arrayOf(InputEvent::class.java, Integer.TYPE))
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    fun injectMotionEvent(
        inputSource: Int, action: Int, clicktime: Long, x: Float, y: Float,
        pressure: Float
    ) {
        val event =
            MotionEvent.obtain(clicktime, clicktime, action, x, y, pressure, 1.0f, 0, 1.0f, 1.0f, 0, 0)
        event.source = inputSource
        injectInputEventMethod.invoke(im, *arrayOf(event, Integer.valueOf(0)))
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    private fun injectKeyEvent(event: KeyEvent) {
        injectInputEventMethod.invoke(im, *arrayOf(event, Integer.valueOf(0)))
    }


}