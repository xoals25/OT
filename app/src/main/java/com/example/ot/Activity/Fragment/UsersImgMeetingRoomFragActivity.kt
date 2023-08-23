package com.example.ot.Activity.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.Adapter.MeetingRoomMainScreenUsersListAdapter
import com.example.ot.Data.MeetingRoomUsersData
import com.example.ot.HandlerTypeCode
import com.example.ot.R
import java.util.*

/**
 * 회의에 입장한 유저들의 프로필 이미지를 보여주는 화면
 *
 * @author kevin
 * @version 1.0, 프로필 이미지 증가
 * @see None

 */
class UsersImgMeetingRoomFragActivity(var meetingRoomActivity: MeetingRoomActivity) : Fragment() {
    var TAG = "UsersImgMeetingRoomFragActivity"
    var userlistRecyclerview:RecyclerView? = null
    var user_list: ArrayList<MeetingRoomUsersData>? = null
    var usersListAdapter:MeetingRoomMainScreenUsersListAdapter? = null
    var gridLayoutManager: GridLayoutManager? = null

    var userlistRecyclerviewNullCk = true

    init {
        user_list = ArrayList<MeetingRoomUsersData>()
        usersListAdapter = MeetingRoomMainScreenUsersListAdapter(
            user_list!!,
            meetingRoomActivity.applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v: View = inflater.inflate(
            R.layout.activity_meetingroom_frag_usersimg,
            container,
            false
        )
        initSettings(v)
        v.setOnClickListener {
            meetingRoomActivity.setSelectMenuLayoutOnOff()
        }
        return v
    }

    /*초기 설정*/
    fun initSettings(v: View){
        userlistRecyclerview = v.findViewById(R.id.recyclerview_meetingroom_frag_usersimg)
        if(userlistRecyclerviewNullCk) {
            gridLayoutManager = GridLayoutManager(meetingRoomActivity.applicationContext, 1)
        }
        else {
            //코드변경 spanCount 2->1
            gridLayoutManager = GridLayoutManager(meetingRoomActivity.applicationContext, 2)
        }
//        gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                when (position) {
//                    0 -> return 1
//                    else -> return 2
//                }
//            }
//        }
        userlistRecyclerview!!.layoutManager = gridLayoutManager
        userlistRecyclerview!!.adapter = usersListAdapter
    }

    fun setuserlistRecyclerviewNullCk(userlistRecyclerviewNullCk:Boolean){
        this.userlistRecyclerviewNullCk = userlistRecyclerviewNullCk
    }

    fun usersListAdapterDataSetChanged(){
        usersListAdapter!!.notifyDataSetChanged()
    }

    fun addUser(data:MeetingRoomUsersData){
        user_list!!.add(data)
//        usersListAdapter!!.notifyDataSetChanged()
    }

    /*추후에 고유 번호 넣어주면 수정해주기*/
    fun removeUser(uniqueValue:String){
        for ((index,item) in user_list!!.withIndex()){
            if(item.uniquenum==uniqueValue){
                user_list!!.removeAt(index)
                break
            }
        }
    }

    fun addUserHelpHandler(data:MeetingRoomUsersData){
        user_list!!.add(data)
        meetingRoomActivity.meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.USER_LIST_CHANGE_MESSAGE)
    }

    /*추후에 고유 번호 넣어주면 수정해주기*/
    fun removeUserHelpHandler(data:MeetingRoomUsersData){
        for ((index,item) in user_list!!.withIndex()){
            if(item.name==data.name){
                user_list!!.removeAt(index)
                meetingRoomActivity.meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.USER_LIST_CHANGE_MESSAGE)
                break
            }
        }
    }
}