package com.example.ot.Activity.Http.Setting

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MeetingPasswordChange {
    @FormUrlEncoded
    @POST("meetingPasswordChange")
    fun meetingPasswordChangeRequest(
        @FieldMap param: HashMap<String, Any>
    ): Call<MeetingPasswordChangeData?>?
}