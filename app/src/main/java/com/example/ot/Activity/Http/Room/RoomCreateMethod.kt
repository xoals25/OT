package com.example.ot.Activity.Http.Room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.MeetingRoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

/**
 * 회의 만들기 할때,사용되는 레트로핏 통신에서 서버에 통신할 메소드를 갖고있음
 *
 * @author kevin
 * @version 1.0
 * @see None

 */

object RoomCreateMethod {
    val TAG ="RoomCreateMethod"
    fun roomCreate(
        roomUniqueValue: String,
        userUniqueValue: String,
        autoAccept:String,
        roomPassWord: String,
        userEmail: String,
        userName: String,
        socialPath: String,
        userImgPath: String,
        context: Context
    ) {
        val roomCreate: RoomCreate = ApiClient.getApiClient()!!.create(RoomCreate::class.java)
        val param = HashMap<String, Any>()
        param["roomUniqueValue"] = roomUniqueValue
        param["autoAccept"] = autoAccept
        param["roomPassWord"] = roomPassWord
        param["roomCreateUserUniqueValue"] = userUniqueValue
        Log.d("TAG", "roomCreate: userUniqueValue : " + userUniqueValue)
        val call: Call<RoomCreateData> = roomCreate.postData(param)
        call.enqueue(object : Callback<RoomCreateData> {
            override fun onResponse(
                call: Call<RoomCreateData>,
                response: Response<RoomCreateData>
            ) {
                if (response.isSuccessful()) {
                    val resultck: Boolean = response.body()!!.result
                    if (resultck == true) {
                        var intent = Intent(context, MeetingRoomActivity::class.java)
                        intent.putExtra("roomNum", roomUniqueValue)
                        intent.putExtra("roomPassWord", roomPassWord)
                        intent.putExtra("roomCreateUserName", userName)
                        intent.putExtra("hostUniqueValue", userUniqueValue)

                        intent.putExtra("userUniqueValue", userUniqueValue)
                        intent.putExtra("userName", userName)
                        intent.putExtra("userEmail", userEmail)
                        intent.putExtra("socialPath", socialPath)
                        intent.putExtra("userProfileImgPath", userImgPath)
                        intent.putExtra("hostCk", "host")
                        context.startActivity(intent)
                    } else {

                    }
                } else {
                }
            }

            override fun onFailure(call: Call<RoomCreateData>, t: Throwable) {
                Log.d(TAG, "onFailure: 실패 t : $t")
            }
        })
    }
}