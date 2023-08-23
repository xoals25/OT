package com.example.ot.Activity.Http.Setting

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SettingLeaveData(
    @Expose @SerializedName("result") val result: Boolean
)
