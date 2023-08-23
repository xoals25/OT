package com.example.ot.Activity.Http.Room

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.MeetingRoomActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

/**
 * 생성일 : 2021 - 03 - 29
 * 회의 나갈 때,사용되는 레트로핏 통신에서 서버에 통신할 메소드를 갖고있음
 *
 * @author kevin
 * @version 1.0
 * @see None
 *
 */

object RoomExitMethod {
    var TAG = "RoomExitMethod"
    fun roomExit(roomUniqueValue:String,meetingRoomActivity:MeetingRoomActivity) {
        val roomExit: RoomExit = ApiClient.getApiClient()!!.create(RoomExit::class.java)
        val param = HashMap<String, Any>()
        param["roomUniqueValue"] = roomUniqueValue
        val call: Call<RoomExitData> = roomExit.postData(param)
        call.enqueue(object : Callback<RoomExitData> {
            override fun onResponse(call: Call<RoomExitData>, response: Response<RoomExitData>) {
                if (response.isSuccessful()) {
                    val resultck: Boolean = response.body()!!.result
                    if (resultck == true) {
                        meetingRoomActivity.finish()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<RoomExitData>, t: Throwable) {
                Log.d(TAG, "onFailure: 실패 t : $t")
            }
        })
    }
}