package com.example.ot.Data

import android.os.Parcel
import android.os.Parcelable

/**
 * MeetingRoomUserListActivity에서 대기자 목록의 데이터가 되는 클래스이다.
 *
 * @author kevin
 * @version 1.0, 데이터 구성
 * @see None
 *
 * @param name 는 사용자에게 보여질 닉네임
 * @param imgPath 는 프로필 이미지를 불러올 경로
 * @param uniquenum 는 사용자 고유번호
 */
data class MeetingRoomWaitUsersData(
    var name: String?, var imgPath: String?,
    var uniquenum: String?,
    var hostAndClient: String?,
    var userSocketId:String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
        ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeString(imgPath)
        dest.writeString(uniquenum)
        dest.writeString(hostAndClient)
        dest.writeString(userSocketId)
    }

    companion object CREATOR : Parcelable.Creator<MeetingRoomWaitUsersData> {
        override fun createFromParcel(parcel: Parcel): MeetingRoomWaitUsersData {
            return MeetingRoomWaitUsersData(parcel)
        }

        override fun newArray(size: Int): Array<MeetingRoomWaitUsersData?> {
            return arrayOfNulls(size)
        }
    }

}