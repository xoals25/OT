package com.example.ot.Activity.Fragment

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.Http.Setting.*
import com.example.ot.Activity.SocialLoginActivity
import com.example.ot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_frag_settings.*
import kotlinx.android.synthetic.main.activity_main_frag_settings.view.*
import kotlinx.android.synthetic.main.activity_meetingroom.*
import kotlinx.android.synthetic.main.activity_setting_sub_meeting_configure.*
import kotlinx.android.synthetic.main.activity_setting_sub_profile.*
import kotlinx.android.synthetic.main.activity_setting_sub_profile.view.*
import kotlinx.android.synthetic.main.alert_dialog_setting_leave.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * 2021.03.12
 * 설명: 희의방(방만들때 기본으로 설정되는 값들) 설정,유저정보 설정을 할 수 있는 클래스
 * 작성자: kevin
 */
class SettingsMainFragActivity : Fragment() {

    /** 소셜 로그인-구글(GoogleSignInClient: GoogleSignInClient: 구글 서비스-'로그인')을 이용하기 위해 선언 */
    private lateinit var googleSignInClient: GoogleSignInClient
    var v: View? = null
    var autoAccept: Boolean? = null
    var autoAcceptRadioButtonCk = true;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.activity_main_frag_settings, container, false)

        sharedInfo(v!!)
        meetingAutoAllow(v!!)
        settingSave(v!!)
        editTextUnderLine(v!!)
        signOut(v!!)
        leave(v!!)
        return v!!
    }

    override fun onResume() {
        super.onResume()
        if (autoAcceptRadioButtonCk) {
            val pref = context!!.getSharedPreferences("loginInfo",
                AppCompatActivity.MODE_PRIVATE)
            val autoAcceptConfirm = pref.getString("autoAccept", "0")
            val nameConfirm = pref.getString("name", "0")
            val passwordConfirm = pref.getString("roomPassWord", "0")

            v!!.textView_setting_userName.setText(nameConfirm)
            v!!.textView_meeting_pass.setText(passwordConfirm)
            if (autoAcceptConfirm == "true") {
                Log.d("스위치 클릭 여부2: ", autoAcceptConfirm)
                v!!.switch_meeting_auto_allow.isChecked = true
            } else {
                v!!.switch_meeting_auto_allow.isChecked = false
                Log.d("스위치 클릭 여부1", "meetingAutoAllow: 스위치 클릭 여부 1")
            }
        }
    }

    /** onActivityResult 분석 필요해서 주석 처리 */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("TAG", "onActivityResult: 확인1")
//        if (requestCode == subProfileRequestCode) {
//            Log.d("TAG", "onActivityResult: 확인2")
//            if (resultCode != Activity.RESULT_OK) {
//                Log.d("TAG", "onActivityResult: 확인3")
//                return
//            } else {
//                Log.d("TAG", "onActivityResult: 확인4")
//                var userName = data!!.getStringExtra("userName")
//                v!!.textView_setting_userName.text = userName
//            }
//        }
//    }

    private fun editTextUnderLine(v: View) {
        v.textView_setting_userName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                imageView_user_name.setImageResource(R.drawable.main_fragment_setting_edit_change_icon)
            } else {
                imageView_user_name.setImageResource(R.drawable.main_fragment_setting_change_icon)
            }
        }

        v.textView_meeting_pass.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                imageView_meeting_pass.setImageResource(R.drawable.main_fragment_setting_edit_change_icon)
            } else {
                imageView_meeting_pass.setImageResource(R.drawable.main_fragment_setting_change_icon)
            }
        }
    }

    /** setting(사용자 이름, 회의 방 비밀번호, 참가자 자동 수락)을 저장하기 위한 메소드*/
    private fun settingSave(v: View) {
        v.button_main_setting_save.setOnClickListener {
            if (v.textView_setting_userName.text.toString() == "") {
                Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (v.textView_meeting_pass.text.toString() == "") {
                Toast.makeText(context, "암호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                closeSoftInput(context!!)
                val settingSave: SettingSave =
                    ApiClient.getApiClient()!!.create(SettingSave::class.java)
                var pref = context!!.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
                val param: HashMap<String, Any> = HashMap()
                param["name"] = v.textView_setting_userName.text.toString()
                param["password"] = v.textView_meeting_pass.text.toString()
                param["autoAccept"] = autoAccept.toString()
                param["uniqueValue"] = pref.getString("uniqueValue", "0").toString()
                val call: Call<SettingSaveData?>? =
                    settingSave.settingSaveRequest(param)
                call!!.enqueue(object : Callback<SettingSaveData?> {
                    override fun onResponse(
                        call: Call<SettingSaveData?>?,
                        response: Response<SettingSaveData?>
                    ) {
                        val userInfoCheck: Boolean = response.body()!!.result
                        if (userInfoCheck) {
                            v.textView_setting_userName.isFocusable = false
                            v.textView_meeting_pass.isFocusable = false

                            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                            val pref = context!!.getSharedPreferences("loginInfo",
                                AppCompatActivity.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("name", v.textView_setting_userName.text.toString())
                            editor.putString("roomPassWord",
                                v.textView_meeting_pass.text.toString())
                            editor.putString("autoAccept", autoAccept.toString())
                            editor.putString("uniqueValue",
                                pref.getString("uniqueValue", "0").toString())
                            editor.commit()
                            v.textView_setting_userName.isFocusable = true
                            v.textView_setting_userName.isFocusableInTouchMode = true
                            v.textView_meeting_pass.isFocusable = true
                            v.textView_meeting_pass.isFocusableInTouchMode = true
                        }
                    }

                    override fun onFailure(call: Call<SettingSaveData?>?, t: Throwable) {
                        Log.d("TAG", "onFailure: 확인 error messageL: t : " + t.toString())
                    }
                })
            }
        }
    }

    /** 키보드 내리는 메소드 */
    private fun closeSoftInput(context: Context) {
        val inputMethodManager = context
            .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager != null
            && (context as Activity).currentFocus != null
        ) {
            inputMethodManager.hideSoftInputFromWindow(context
                .currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
            imageView_user_name.setImageResource(R.drawable.main_fragment_setting_change_icon)
            imageView_meeting_pass.setImageResource(R.drawable.main_fragment_setting_change_icon)
        }
    }


    /** Shared 'loginInfo'의 데이터를 불러와 각 View에 맞게 데이터를 집어넣어 주는 메소드*/
    private fun sharedInfo(v: View) {
        var pref = context!!.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
        val name = pref.getString("name", "0")
        val email = pref.getString("email", "0")
        val meetingPassword = pref.getString("roomPassWord", "0")
        val profileImgPath = pref.getString("profileImgPath", "0")
        Log.d("profileImgPath", profileImgPath.toString())
        val uri = Uri.parse(profileImgPath)
        Glide.with(context!!)
            .load(uri)
            .into(v.cardView_frag_setting_sub_profile)
        v.textView_setting_userName.setText(name)
        v.textView_user_email.text = email
        v.textView_meeting_pass.setText(meetingPassword)
    }

    /** 참가자 자동 수락 switch 에 따라 자동 수락을 true 로 할 지 false 로 할 지 결정해준다. */
    private fun meetingAutoAllow(v: View) {
        val pref = context!!.getSharedPreferences("loginInfo",
            AppCompatActivity.MODE_PRIVATE)
        val autoAcceptConfirm = pref.getString("autoAccept", "0")
        if (autoAcceptConfirm == "true") {
            Log.d("스위치 클릭 여부2: ", autoAcceptConfirm)
            v.switch_meeting_auto_allow.isChecked = true
        } else {
            v.switch_meeting_auto_allow.isChecked = false
            Log.d("스위치 클릭 여부1", "meetingAutoAllow: 스위치 클릭 여부 1")
        }

        v.switch_meeting_auto_allow.setOnClickListener {
            autoAcceptRadioButtonCk = false
            if (v.switch_meeting_auto_allow.isChecked) {
                autoAccept = true
                Log.d("tag", autoAccept.toString())
            } else {
                autoAccept = false
                Log.d("tag", autoAccept.toString())
            }
        }
    }

    /** 로그아웃 버튼을 눌렀을 경우 FirebaseAuth.와 GoogleSignInClient.로 로그인 정보를 초기화해주는 동시에 소셜 로그인 화면으로 전환해주는 메소드*/
    private fun signOut(v: View) {
        v.textView_logout.setOnClickListener {
            //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context!!, gso)

            FirebaseAuth.getInstance().signOut()
            googleSignInClient.signOut()
            val pref = context!!.getSharedPreferences("loginInfo", AppCompatActivity.MODE_PRIVATE)
            val editor = pref.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(
                activity, SocialLoginActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /** 회원 탈퇴 버튼을 눌렀을 경우 */
    private fun leave(v: View) {
        v.textView_leave.setOnClickListener {

            val builder = AlertDialog.Builder(context!!)

            val view: View = LayoutInflater.from(context!!)
                .inflate(R.layout.alert_dialog_setting_leave, null, false)
            builder.setView(view)

            val dialog = builder.create()

            view.button_leave_yes.setOnClickListener {
                val settingSave: SettingLeave =
                    ApiClient.getApiClient()!!.create(SettingLeave::class.java)
                var pref = context!!.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
                val param: HashMap<String, Any> = HashMap()
                param["uniqueValue"] = pref.getString("uniqueValue", "0").toString()
                Log.d("값들왔나", pref.getString("uniqueValue", "0").toString())
                val call: Call<SettingLeaveData?>? =
                    settingSave.settingLeaveRequest(param)
                call!!.enqueue(object : Callback<SettingLeaveData?> {
                    override fun onResponse(
                        call: Call<SettingLeaveData?>?,
                        response: Response<SettingLeaveData?>
                    ) {
                        val userInfoCheck: Boolean = response.body()!!.result
                        if (userInfoCheck) {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()

                            googleSignInClient = GoogleSignIn.getClient(context!!, gso)

                            FirebaseAuth.getInstance().signOut()
                            googleSignInClient.signOut()
                            val pref = context!!.getSharedPreferences("loginInfo", AppCompatActivity.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.clear()
                            editor.commit()
                            val intent = Intent(
                                activity, SocialLoginActivity::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            dialog.dismiss()
                        }
                    }
                    override fun onFailure(call: Call<SettingLeaveData?>?, t: Throwable) {
                        Log.d("TAG", "onFailure: 확인 error messageL: t : $t")
                    }
                })
            }
            view.button_leave_no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: ")
        autoAcceptRadioButtonCk = true
    }
}