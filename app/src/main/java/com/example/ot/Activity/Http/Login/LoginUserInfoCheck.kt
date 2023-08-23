package com.example.ot.Activity.Http.Login

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 2021.03.18
 * 설명: 백엔드에서 처리 해야 될 로그인 데이터들을 서버 login 경로로 전송해주는 클래스입니다.
 * 작성자: socical
 */

interface LoginUserInfoCheck {
    @FormUrlEncoded
    @POST("login")
    fun loginRequest(
        @FieldMap param: HashMap<String, Any>
    ): Call<LoginUserInfoCheckData?>?
}