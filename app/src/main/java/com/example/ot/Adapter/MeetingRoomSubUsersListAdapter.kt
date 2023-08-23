package com.example.ot.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ot.Activity.MeetingRoomUserListActivity
import com.example.ot.Data.MeetingRoomUsersData
import com.example.ot.R
import java.util.*
/**
 * 2021 -04 -27
 *
 *
 * @author kevin
 * @version 1.0, 데이터 셋팅
 * @see None

 */
class MeetingRoomSubUsersListAdapter(
    var user_list:ArrayList<MeetingRoomUsersData>, var context:Context,
    var meetingRoomUserListActivity: MeetingRoomUserListActivity
) :RecyclerView.Adapter<MeetingRoomSubUsersListAdapter.CustomViewHolder>() {
    var TAG = "Adapter_Invite_Friend"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_meetingroom_sub_user_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if(user_list[position].imgPath=="없음"){
            holder.userProfileImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profileimg))
//            holder.userProfileImg.background = ContextCompat.getDrawable(context,R.drawable.imageview_corner_rounding)
//            holder.userProfileImg.clipToOutline = true
        }
        else{
            //추후에 이미지경로 받으면 넣어줄곳
            val uri = Uri.parse(user_list[position].imgPath)
            Glide.with(context)
                .load(uri)
                .into(holder.userProfileImg)
//            holder.userProfileImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profileimg))
//            holder.userProfileImg.background = ContextCompat.getDrawable(context,R.drawable.imageview_corner_rounding)
//            holder.userProfileImg.clipToOutline = true
        }
        holder.userNickname.text= user_list[position].name
        if(user_list[position].hostAndClient=="host"){
            if(meetingRoomUserListActivity.userUniqueValue==user_list[position].uniquenum){
                holder.nameWho.text = "(호스트,본인)"
            }
            else{
                holder.nameWho.text="(호스트)"
            }
        }
        else{
            if(meetingRoomUserListActivity.userUniqueValue==user_list[position].uniquenum){
                holder.nameWho.text = "(본인)"
            }
            else{
                holder.nameWho.text=""
            }
        }
        holder.rootView.tag = position
    }

    override fun getItemCount(): Int {
        return user_list.size
    }

    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var userProfileImg: ImageView = v.findViewById(R.id.imageview_user_list_profile)
        var userNickname: TextView = v.findViewById(R.id.textview_user_list_name)
        var nameWho:TextView =v.findViewById(R.id.textview_user_list_name_who)
        var rootView :View = v
    }


}
