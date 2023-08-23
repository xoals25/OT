package com.example.ot.Activity.Http.Room

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 회의 참가할 때,사용되는 레트로핏 통신에서 서버로 받을 데이터양식을 표현해주는 데이터 클래스
 *
 * @author kevin
 * @version 1.0,
 * @see None
 *
 * @param result 는 결과값 true면 성공 false면 실패
 * @param roomCreateUserName 는 결과값이 true면 받아오는 방 개설자 이름 ->추후에 방 정보에 넣어줄 데이터
 */
data class RoomEnterData(
    @Expose@SerializedName("result") val result:Boolean,
    @Expose@SerializedName("roomCreateUserName") val roomCreateUserName:String,
    @Expose@SerializedName("autoAccept") val autoAccept:Boolean
) {
}