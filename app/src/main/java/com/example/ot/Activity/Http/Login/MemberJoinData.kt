package com.example.ot.Activity.Http.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MemberJoinData(
    @Expose @SerializedName("result") val result: Boolean,
    @Expose @SerializedName("roomPassWord") val roomPassWord: String
) {
}