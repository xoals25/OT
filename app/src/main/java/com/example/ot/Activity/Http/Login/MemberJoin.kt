package com.example.ot.Activity.Http.Login

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MemberJoin {
    @FormUrlEncoded
    @POST("signUp")
    fun joinRequest(
        @FieldMap param: HashMap<String, Any>
    ): Call<MemberJoinData?>?
}