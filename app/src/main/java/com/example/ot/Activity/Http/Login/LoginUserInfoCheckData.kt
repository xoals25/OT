package com.example.ot.Activity.Http.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 2021.03.18
 * 설명: 서버에서 받은 소셜 로그인 정보로 UserData 테이블의 정보를 가지고 오는 클래스입니다.
 * 작성자: socical
 */
data class LoginUserInfoCheckData(
    @Expose@SerializedName("result") val result:Boolean,
    @Expose@SerializedName("email") val email:String,
    @Expose@SerializedName("nickname") val nickname:String,
    @Expose@SerializedName("profileImgPath") val profileImgPath:String,
    @Expose@SerializedName("uniqueValue") val uniqueValue:String,
    @Expose@SerializedName("socialPath") val socialPath:String,
    @Expose@SerializedName("roomPassWord") val roomPassWord:String,
    @Expose@SerializedName("autoAccept") val autoAccept:String
) {
}