package com.example.ot.Activity.Http.Room

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.ot.Activity.Fragment.JoinMainFragActivity
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.Activity.WaitRoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

/**
 * 회의 참가할 때,사용되는 레트로핏 통신에서 서버에 통신할 메소드를 갖고있음
 *
 * @author kevin
 * @version 1.0
 * @see None

 */

object RoomEnterMethod {
    var TAG = "RoomEnterMethod"
    fun roomEnter(
        roomUniqueValue:String, roomPassWord:String,
        userUniqueValue:String, userEmail:String,
        userName:String,
        socialPath:String,
        userImgPath:String,
        dialog: Dialog,
        context: Context, joinMainFragActivity: JoinMainFragActivity
    ) {
        val roomEnter: RoomEnter = ApiClient.getApiClient()!!.create(RoomEnter::class.java)
        val param = HashMap<String, Any>()
        param["roomUniqueValue"] = roomUniqueValue
        param["roomPassWord"] = roomPassWord
        val call: Call<RoomEnterData> = roomEnter.postData(param)
        call.enqueue(object : Callback<RoomEnterData> {
            override fun onResponse(call: Call<RoomEnterData>, response: Response<RoomEnterData>) {
                if (response.isSuccessful()) {
                    val resultck: Boolean = response.body()!!.result
                    if (resultck == true) {
                        val autoAccept = response.body()!!.autoAccept
                        val roomCreateUserName: String = response.body()!!.roomCreateUserName

                        if(autoAccept==true){
                            var intent = Intent(context, MeetingRoomActivity::class.java)
                            intent.putExtra("autoAccept","on")
                            intent.putExtra("roomNum", roomUniqueValue)
                            intent.putExtra("roomPassWord", roomPassWord)
                            intent.putExtra("roomCreateUserName", roomCreateUserName)
                            intent.putExtra("hostUniqueValue", "")
                            intent.putExtra("userUniqueValue", userUniqueValue)
                            intent.putExtra("userName", userName)
                            intent.putExtra("userEmail", userEmail)
                            intent.putExtra("socialPath", socialPath)
                            intent.putExtra("userProfileImgPath", userImgPath)
                            dialog.dismiss()
                            context.startActivity(intent)
                        }
                        else{
                            var intent = Intent(context, MeetingRoomActivity::class.java)
//                            var intent = Intent(context, WaitRoomActivity::class.java)
                            intent.putExtra("autoAccept","off")
                            intent.putExtra("roomNum", roomUniqueValue)
                            intent.putExtra("roomPassWord", roomPassWord)
                            intent.putExtra("roomCreateUserName", roomCreateUserName)
                            intent.putExtra("hostUniqueValue", "")
                            intent.putExtra("userUniqueValue", userUniqueValue)
                            intent.putExtra("userName", userName)
                            intent.putExtra("userEmail", userEmail)
                            intent.putExtra("socialPath", socialPath)
                            intent.putExtra("userProfileImgPath", userImgPath)
                            dialog.dismiss()
//                            context.startActivity(intent)
                            joinMainFragActivity.startActivityForResult(intent,100)
                        }
                    } else {
                        Log.d(TAG, "onResponse: 일치하는 정보 없음")
                        Toast.makeText(context, "입력하신 정보와 일치하는 방이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<RoomEnterData>, t: Throwable) {
                Log.d(TAG, "onFailure: 실패 t : $t")
            }
        })
    }
}