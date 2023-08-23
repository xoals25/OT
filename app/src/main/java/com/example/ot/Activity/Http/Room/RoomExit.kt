package com.example.ot.Activity.Http.Room

import com.example.ot.Activity.Http.Login.LoginUserInfoCheckData
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 생성일 : 2021 - 03 - 29
 * 회의 나갈 때,사용되는 레트로핏 통신에서 서버로 보내줄 데이터양식을 갖고있는 인터페이스
 *
 * @author kevin
 * @version 1.0,
 * @see None

 */
interface RoomExit {
    @FormUrlEncoded
    @POST("roomExit")
    fun postData(
        @FieldMap param: HashMap<String, Any>
    ): Call<RoomExitData>
}