package com.example.ot.Activity.Http.Room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 회의 만들기 할때,사용되는 레트로핏 통신에서 서버로 받을 데이터양식을 표현해주는 데이터 클래스
 *
 * @author kevin
 * @version 1.0,
 * @see None

 */
data class RoomCreateData(
    @Expose@SerializedName("result") val result:Boolean
) {
}