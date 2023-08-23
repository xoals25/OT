package com.example.ot.Activity.Http.Room

import com.example.ot.Activity.Http.Login.LoginUserInfoCheckData
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 회의 만들기 할때,사용되는 레트로핏 통신에서 서버로 보내줄 데이터양식을 갖고있는 인터페이스
 *
 * @author kevin
 * @version 1.0,
 * @see None

 */
interface RoomCreate {
    @FormUrlEncoded
    @POST("roomCreate")
    fun postData(
        @FieldMap param: HashMap<String, Any>
    ): Call<RoomCreateData>
}