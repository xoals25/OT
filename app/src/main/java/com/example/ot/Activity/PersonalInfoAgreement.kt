package com.example.ot.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.Http.Login.MemberJoin
import com.example.ot.Activity.Http.Login.MemberJoinData
import com.example.ot.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_personal_info_agreement.*
import kotlinx.android.synthetic.main.activity_setting_sub_profile.*
import kotlinx.android.synthetic.main.activity_setting_sub_profile.linearLayout_setting_logout
import kotlinx.android.synthetic.main.activity_social_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInfoAgreement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info_agreement)

        sinUpCk()
    }

    /** 약관 동의 체크 여부에 따라서 가입 승인을 해주는 메소드*/
    private fun sinUpCk() {
        var socialUserNickName: String? = intent.getStringExtra("socialUserNickName")
        var socialUserEmail: String? = intent.getStringExtra("socialUserEmail")
        var uniqueValue: String? = intent.getStringExtra("uniqueValue")
        var socialUserProfileImgPath: String? = intent.getStringExtra("socialUserProfileImgPath")
        var socialPath: String? = intent.getStringExtra("socialPath")

        btn_join.setOnClickListener {
            if (ckBox_personal_info_agreement.isChecked) {
                val memberJoin: MemberJoin =
                    ApiClient.getApiClient()!!.create(MemberJoin::class.java)
                val param: HashMap<String, Any> = HashMap()
                param["nickname"] = socialUserNickName!!
                param["email"] = socialUserEmail!!
                param["uniqueValue"] = uniqueValue!!
                param["profileImgPath"] = socialUserProfileImgPath!!
                param["socialPath"] = socialPath!!
                val call: Call<MemberJoinData?>? = memberJoin.joinRequest(param)
                call!!.enqueue(object : Callback<MemberJoinData?> {
                    override fun onResponse(
                        call: Call<MemberJoinData?>?,
                        response: Response<MemberJoinData?>
                    ) {
                        val userInfoCheck: Boolean = response.body()!!.result
                        if (userInfoCheck) {

                            val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("name", socialUserNickName!!)
                            editor.putString("email", socialUserEmail!!)
                            editor.putString("profileImgPath", socialUserProfileImgPath!!)
                            editor.putString("uniqueValue", uniqueValue!!)
                            editor.putString("socialPath", socialPath!!)
                            editor.putString("roomPassWord", response.body()!!.roomPassWord)
                            editor.putString("autoAccept", "false")
                            editor.commit()

                            val signUp = Intent(
                                applicationContext, MainActivity::class.java
                            )
                            signUp.putExtra("socialUserNickName", socialUserNickName)
                            signUp.putExtra("socialUserEmail", socialUserEmail)
                            signUp.putExtra("uniqueValue", uniqueValue)
                            signUp.putExtra("socialUserProfileImgPath", socialUserProfileImgPath)
                            signUp.putExtra("socialPath", socialPath)
                            signUp.putExtra("roomPassWord", response.body()!!.roomPassWord)
                            signUp.putExtra("autoAccept", "false")
                            startActivity(signUp)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<MemberJoinData?>?, t: Throwable) {
                        Log.d("TAG", "onFailure: 확인 error messageL: t : " + t.toString())
                    }
                })
            } else {
                Snackbar.make(ConstraintLayout_Join, "개인 정보 동의해 주세요.", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
}