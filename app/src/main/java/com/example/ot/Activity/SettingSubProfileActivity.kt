package com.example.ot.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.Http.Login.MemberJoin
import com.example.ot.Activity.Http.Login.MemberJoinData
import com.example.ot.Activity.Http.Setting.UserNameChange
import com.example.ot.Activity.Http.Setting.UserNameChangeData
import com.example.ot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_frag_settings.*
import kotlinx.android.synthetic.main.activity_main_frag_settings.view.*
import kotlinx.android.synthetic.main.activity_setting_sub_profile.*
import kotlinx.android.synthetic.main.alert_dialog_main_frag_join.view.*
//import kotlinx.android.synthetic.main.alert_dialog_setting_sub_profile_user_name_chg.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * 앱을 참여하고 있는 회의 화면에서 화면 공유를 눌렀을 때 나오는 Activity
 *
 * @author socical
 * @date 2021.03.12
 * @version 1.0, 로그아웃 기능 추가(구글)
 *
 */

class SettingSubProfileActivity : AppCompatActivity() {

    /** 소셜 로그인-구글(GoogleSignInClient: GoogleSignInClient: 구글 서비스-'로그인')을 이용하기 위해 선언 */
    private lateinit var googleSignInClient: GoogleSignInClient
    var changeName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_sub_profile)

        sharedInfo()
//        userNameChange()
        signOut()
    }


    /** sharedPreference(로컬)의 loginInfo(사용자 로그인 정보)에 저장된 프로필 경로를 가지고와 Glide.로 이미지를 띄우는 메소드 */
    private fun sharedInfo() {
        val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
        val profileImgPath = pref.getString("profileImgPath", "0")
        val uri = Uri.parse(profileImgPath)
        Glide.with(applicationContext)
            .load(uri)
            .into(cardView_setting_sub_profile)
    }

    /** 로그아웃 버튼을 눌렀을 경우 FirebaseAuth.와 GoogleSignInClient.로 로그인 정보를 초기화해주는 동시에 소셜 로그인 화면으로 전환해주는 메소드*/
    private fun signOut() {
        linearLayout_setting_logout.setOnClickListener {
            //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            FirebaseAuth.getInstance().signOut()
            googleSignInClient.signOut()
            val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
            val editor = pref.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(
                this, SocialLoginActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /** 사용자 이름 변경 버튼을 눌렀을 경우 EditText의 Tint에 기존 사용자 이름과 변경 버튼이 있는 다이얼로그가 출력이 된다. */
//    private fun userNameChange() {
//        linearLayout_setting_userName_chg.setOnClickListener {
//
//            val builder = AlertDialog.Builder(this)
//
//            val view: View = LayoutInflater.from(this)
//                .inflate(R.layout.alert_dialog_setting_sub_profile_user_name_chg, null, false)
//            builder.setView(view)
//
//            val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
//            val userName = pref.getString("name", "0")
//
//            val userNameEditText = view.findViewById<View>(R.id.editText_user_name_chg) as EditText
//            userNameEditText.hint = userName
//
//            val userNameChgButton = view.findViewById<View>(R.id.button_user_name_chg) as Button
//
//            val dialog = builder.create()
//
//            userNameChgButton.setOnClickListener {
//                val userNameChg = userNameEditText.text.toString()
//                Log.d("변경 할 사용자 이름",userNameChg.toString())
//                when {
//                    userNameChg == "" -> {
//                        Toast.makeText(
//                            this,
//                            "변경하실 이름을 입력해주세요.",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//                    else -> {
//                        val userNameChange: UserNameChange =
//                            ApiClient.getApiClient()!!.create(UserNameChange::class.java)
//
//                        val param: HashMap<String, Any> = HashMap()
//                        Log.d("TAG","들어옴111111111")
//                        param["name"] = userNameChg
//                        Log.d("사용자 이름11",userNameChg)
//                        param["uniqueValue"] = pref.getString("uniqueValue", "0").toString()
//                        Log.d("사용자 이름22",pref.getString("uniqueValue", "0").toString())
//                        Log.d("TAG","들어옴2222222")
//                        val call: Call<UserNameChangeData?>? = userNameChange.userNameChangeRequest(param)
//                        call!!.enqueue(object : Callback<UserNameChangeData?> {
//                            override fun onResponse(
//                                call: Call<UserNameChangeData?>?,
//                                response: Response<UserNameChangeData?>
//                            ) {
//                                Log.d("TAG", "들어옴333333")
//                                Log.d("TAG", "onResponse11: ${response.isSuccessful}")
//                                val userInfoCheck: Boolean = response.body()!!.result
//                                if (userInfoCheck) {
//                                    val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
//                                    val editor = pref.edit()
//                                    editor.putString("name", userNameChg)
//                                    editor.commit()
//
//                                    changeName = userNameChg
//                                    //http 통신으로 DB update
//                                    dialog.dismiss()
//                                }
//                            }
//
//                            override fun onFailure(call: Call<UserNameChangeData?>?, t: Throwable) {
//                                Log.d("TAG", "onFailure: 확인 error messageL: t : " + t.toString())
//                            }
//                        })
//                    }
//                }
//            }
//            dialog.show()
//        }
//    }

    /**
     * 액티비티 종료 할 때 사용되는 기능 (startActivityForResult)
     * */
    private fun finishForOnActivityResult(){
        var intent = Intent()
        intent.putExtra("userName",changeName)
        setResult(RESULT_OK,intent)
        finish()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finishForOnActivityResult()
    }
}