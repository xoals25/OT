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
import com.example.ot.Data.MeetingRoomWaitUsersData
import com.example.ot.R
import com.example.ot.Socket.MeetingRoomSignalingClient
import java.util.*
/**
 * 2021 -04 -27
 *
 *
 * @author kevin
 * @version 1.0, 데이터 셋팅
 * @see None

 */
class MeetingRoomSubWaitUsersListAdapter(
    var waitUserList:ArrayList<MeetingRoomWaitUsersData>, var context:Context,
    var hostCk:String
) :RecyclerView.Adapter<MeetingRoomSubWaitUsersListAdapter.CustomViewHolder>() {
    var TAG = "Adapter_Invite_Friend"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_meetingroom_sub_user_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if(waitUserList[position].imgPath=="없음"){
            holder.userProfileImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profileimg))
//            holder.userProfileImg.background = ContextCompat.getDrawable(context,R.drawable.imageview_corner_rounding)
//            holder.userProfileImg.clipToOutline = true
        }
        else{
            //추후에 이미지경로 받으면 넣어줄곳
            val uri = Uri.parse(waitUserList[position].imgPath)
            Glide.with(context)
                .load(uri)
                .into(holder.userProfileImg)
//            holder.userProfileImg.background = ContextCompat.getDrawable(context,R.drawable.imageview_corner_rounding)
//            holder.userProfileImg.clipToOutline = true
        }

        holder.userNickname.text= waitUserList[position].name
        holder.rootView.tag = position
    }

    override fun getItemCount(): Int {
        return waitUserList.size
    }

    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var userProfileImg: ImageView = v.findViewById(R.id.imageview_user_list_profile)
        var userNickname: TextView = v.findViewById(R.id.textview_user_list_name)
        var nameWho:TextView =v.findViewById(R.id.textview_user_list_name_who)
        var acceptBtn:TextView =v.findViewById(R.id.button_accept)
        var refuseBtn:TextView =v.findViewById(R.id.button_refuse)
        var rootView :View = v
        init {
            nameWho.visibility = View.GONE
            if(hostCk=="host") {
                acceptBtn.visibility = View.VISIBLE
                refuseBtn.visibility = View.VISIBLE

                acceptBtn.setOnClickListener {
                    var position: Int = rootView.tag as Int
                    MeetingRoomSignalingClient.get()!!.waitUserAcceptOrRefuse(waitUserList[position].userSocketId!!,
                        "accept",
                        waitUserList[position].uniquenum!!)
                    waitUserList.removeAt(position)
                    notifyDataSetChanged()

                }
                refuseBtn.setOnClickListener {
                    var position: Int = rootView.tag as Int
                    MeetingRoomSignalingClient.get()!!.waitUserAcceptOrRefuse(waitUserList[position].userSocketId!!,
                        "refuse",
                        waitUserList[position].uniquenum!!)
                    waitUserList.removeAt(position)
                    notifyDataSetChanged()

                }
            }
        }

    }


}
