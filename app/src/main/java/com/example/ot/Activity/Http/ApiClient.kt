package com.example.ot.Activity.Http

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 2021.03.18
 * 설명: 백엔드에서 처리 해야 될 데이터들을 서버로 전송해주는 클래스입니다.
 * 작성자: socical
 */

class ApiClient {
    companion object {
        private val BASE_URL = "http://3.36.140.214:3000"
        var retrofit: Retrofit? = null
        fun getApiClient(): Retrofit? {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit
        }
    }
}