package com.example.ot.Data

import android.os.Parcel
import android.os.Parcelable

data class ChatItem(
    var name: String?,
    var content: String?, // 0일 시 왼쪽(상대가 보낸 메세지), 2일 시 오른쪽(내가 보낸 메세지)
    var viewType: Int
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeString(content)
        dest.writeInt(viewType)
    }

    companion object CREATOR : Parcelable.Creator<ChatItem> {
        override fun createFromParcel(parcel: Parcel): ChatItem {
            return ChatItem(parcel)
        }

        override fun newArray(size: Int): Array<ChatItem?> {
            return arrayOfNulls(size)
        }
    }

}
