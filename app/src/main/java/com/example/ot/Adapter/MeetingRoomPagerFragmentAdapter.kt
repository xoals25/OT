package com.example.ot.Adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ot.Activity.MeetingRoomActivity
import com.example.ot.HandlerTypeCode
import java.util.*

/**
 * MeetingroomActivity에서 화면들 보여주는 뷰페이저 어답터이다.
 *
 * @author kevin
 * @version 1.0, 어답터 구성 및 뷰페이저 추가,삭제 메소드 작성
 * @see None
 *
 * @param fragmentActivity 는 상속 받은 FragmentStateAdapter에서 사용
 * @param fragmentArrayList 는 보여줄 페이지들을 담아두는 어레이리스트
 */
class MeetingRoomPagerFragmentAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(
    fragmentActivity
) {
    var TAG = "MeetingRoomPagerFragmentAdapter"
    var fragmentArrayList: ArrayList<Fragment>? = null
    var context:MeetingRoomActivity? = null

    constructor(fragmentActivity: FragmentActivity,context:MeetingRoomActivity ,fragmentArrayList: ArrayList<Fragment>) :this(fragmentActivity){
        this.fragmentArrayList = fragmentArrayList
        this.context = context
    }

    override fun getItemCount(): Int {
        return fragmentArrayList!!.size
    }

    override fun createFragment(position: Int): Fragment {

        return fragmentArrayList!![position]
    }




    /**
     * 뷰페이저에 원하는 프래그먼트를 추가하는 메소드이다.
     *
     * @ param fragment 는 추가할 Fragment이다.
     */
    fun addFragment(fragment: Fragment) {
        fragmentArrayList!!.add(fragment)
        notifyDataSetChanged()
    }
    /**
     * 뷰페이저에 원하는 프래그먼트를 제거하는 메소드이다.
     *
     * @ param fragment 는 제거할 Fragment이다.
     */
    fun removeFragment(fragment: Fragment) {
        fragmentArrayList!!.remove(fragment)
        notifyDataSetChanged()
    }

    /**
     * 뷰페이저에 원하는 프래그먼트를 추가하는 메소드이다.(핸들러 필요한 경우)
     *
     * @ param fragment 는 추가할 Fragment이다.
     */
    fun addFragmentHelperHandler(fragment: Fragment) {
        fragmentArrayList!!.add(fragment)
        context!!.meetingRoomHandler?.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.VIEWPAGER_ADD_PAGE_MESSAGE)
    }
    /**
     * 뷰페이저에 원하는 프래그먼트를 제거하는 메소드이다. (핸들러 필요한 경우)
     *
     * @ param fragment 는 제거할 Fragment이다.
     */
    fun removeFragmentHelperHandler(fragment: Fragment) {
        fragmentArrayList!!.remove(fragment)
        context!!.meetingRoomHandler!!.sendEmptyMessage(HandlerTypeCode.MeetingRoomAcitvityType.VIEWPAGER_ADD_PAGE_MESSAGE)
    }
}