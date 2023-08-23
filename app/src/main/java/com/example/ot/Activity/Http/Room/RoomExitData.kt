package com.example.ot.Activity.Http.Room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 생성일 : 2021 - 03 - 29
 * 회의 나갈 때,사용되는 레트로핏 통신에서 서버로 받을 데이터양식을 표현해주는 데이터 클래스
 *
 * @author kevin
 * @version 1.0,
 * @see None
 *
 * @param result 는 결과값 true면 방 파괴 성공 false면 방 파괴 실패

 */
data class RoomExitData(
    @Expose@SerializedName("result") val result:Boolean
) {
}