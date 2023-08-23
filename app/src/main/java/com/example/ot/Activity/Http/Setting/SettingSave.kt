package com.example.ot.Activity.Http.Setting

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SettingSave {
    @FormUrlEncoded
    @POST("settingSave")
    fun settingSaveRequest(
        @FieldMap param: HashMap<String, Any>
    ): Call<SettingSaveData?>?
}