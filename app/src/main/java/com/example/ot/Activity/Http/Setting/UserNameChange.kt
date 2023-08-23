package com.example.ot.Activity.Http.Setting

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserNameChange {
    @FormUrlEncoded
    @POST("userNameChange")
    fun userNameChangeRequest(
        @FieldMap param: HashMap<String, Any>
    ): Call<UserNameChangeData?>?
}