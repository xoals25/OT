package com.example.ot.Data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import com.bumptech.glide.RequestBuilder
import com.example.ot.Activity.MeetingRoomActivity
import java.io.InputStream
import java.net.URL

/**
 * MeetingRoomActivity에서 뷰페이저로 보여질 UsersImgMeetingRoomFragActivity에서
 * 참여한 유저들 리스트 보여줄 리사이클러뷰에 필요한 데이터 클래스이다.
 *
 * @author kevin
 * @version 1.0, 데이터 구성
 * @see None
 *
 * @param name 는 사용자에게 보여질 닉네임
 * @param imgPath 는 프로필 이미지를 불러올 경로
 * @param uniquenum 는 사용자 고유번호
 * @param hostAndClient 는 호스트인지 클라이언트인지 구분 "host","client"
 * @param chatNotiOnOff 는 해당 유저가 채팅을 보냈는지 확인하는 알림 on,off 시켜주는 데이터,
 *                      상대방이 채팅을 했다면 : "on"
 *                      상대방 채팅을 내가 봤거나 ,상대방이 채팅을 안했을 경우 : "off"
 */

data class MeetingRoomUsersData(
    var name: String?, var imgPath: String?,
    var uniquenum: String?,
    var hostAndClient: String?,
    var chatNotiOnOff:String?
) : Parcelable {
    var requestBuilder:RequestBuilder<Drawable>?=null
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    parcel.readString())


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeString(imgPath)
        dest.writeString(uniquenum)
        dest.writeString(hostAndClient)
        dest.writeString(chatNotiOnOff)
    }

    companion object CREATOR : Parcelable.Creator<MeetingRoomUsersData> {
        override fun createFromParcel(parcel: Parcel): MeetingRoomUsersData {
            return MeetingRoomUsersData(parcel)
        }

        override fun newArray(size: Int): Array<MeetingRoomUsersData?> {
            return arrayOfNulls(size)
        }
    }

}