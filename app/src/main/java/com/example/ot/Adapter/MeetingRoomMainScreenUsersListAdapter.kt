package com.example.ot.Adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ot.Data.MeetingRoomUsersData
import com.example.ot.R
import kotlinx.android.synthetic.main.activity_setting_sub_profile.*
import kotlinx.android.synthetic.main.item_meetingroom_mainscreen_user_list.view.*
import java.io.InputStream
import java.net.URL
import java.util.*
/**
 * MeetingRoomActivity에서 뷰페이저로 보여질 UsersImgMeetingRoomFragActivity에서
 * 참여한 유저들 리스트 보여줄 리사이클러뷰와 연결될 어답터 클래스이다.
 *
 * @author kevin
 * @version 1.0, 데이터 셋팅
 * @see None
 *
 * @param user_list 참여한 유저들의 데이터를 갖고있는 어레이리스트
 * @param context 는 어답터가 불러지는 class의 Context이다.
 */
class MeetingRoomMainScreenUsersListAdapter(var user_list:ArrayList<MeetingRoomUsersData>, var context:Context) :RecyclerView.Adapter<MeetingRoomMainScreenUsersListAdapter.CustomViewHolder>() {
    var TAG = "Adapter_Invite_Friend"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_meetingroom_mainscreen_user_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if(user_list[position].imgPath==null){
            Log.d(TAG, "onBindViewHolder: 사진확인인인1")
            holder.userProfileImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profileimg))
        }
        else{
            Log.d(TAG, "onBindViewHolder: 경로 확인 : ${user_list[position].imgPath}")
//            if(user_list[position].requestBuilder==null) {
                val uri = Uri.parse(user_list[position].imgPath)
                Glide.with(context)
                    .load(uri)
                    .into(holder.userProfileImg)
                user_list[position].requestBuilder = Glide.with(context).load(uri)
//            }
//            else{
//                holder.userProfileImg.setImageDrawable(user_list[position].requestBuilder!!.fallbackDrawable)
//            }
        }
        holder.userNickname.text= user_list[position].name

        if(user_list[position].chatNotiOnOff=="on"){
            Log.d(TAG, "onChatting: 유저 데이터 확인 on")
            holder.userChatNoti.visibility=View.VISIBLE
        }
        else{
            Log.d(TAG, "onChatting: 유저 데이터 확인 off")
            holder.userChatNoti.visibility=View.INVISIBLE
        }
        holder.rootView.tag = position
    }

    override fun getItemCount(): Int {
        return user_list.size
    }

    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var userProfileImg: ImageView = v.imageview_item_meetingroom_userprofileImg
        var userNickname: TextView = v.textView_item_meetingroom_usernickname
        var userChatNoti:LinearLayout = v.linearlayout_message_noti_icon
        var rootView :View = v
    }

    init {

    }
    /*
         * String형을 BitMap으로 변환시켜주는 함수
         * */
    private fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

}
