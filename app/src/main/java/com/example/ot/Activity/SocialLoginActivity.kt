package com.example.ot.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ot.Activity.Http.ApiClient
import com.example.ot.Activity.Http.Login.LoginUserInfoCheck
import com.example.ot.Activity.Http.Login.LoginUserInfoCheckData
import com.example.ot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_social_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * 앱을 실행했을 때 나오는 소셜 로그인(구글,카카오) 액티비티
 *
 * @author socical
 * @date 2021.03.21
 * @version 1.0, 구글 소셜 로그인 (자동 로그인 기능, 로그아웃 시 다른 계정으로 로그인 가능)
 *
 */

class SocialLoginActivity : AppCompatActivity() {

    /** 소셜 로그인-구글(FirebaseAuth: google.과 firebase.를 연동하기 위해 선언, GoogleSignInClient: 구글 서비스-'로그인')을 이용하기 위해 선언 */
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 99

    /** 소셜 로그인 정보(socialUserNickName: 유저 이름, socialUserEmail: 유저 이메일, uniqueValue: 유저 회의 방 고유 번호,
     * socialUserProfileImgPath: 유저 프로필 경로, socialPath: 유저 소셜 로그인 경로, roomPassWord: 유저 회의 방 비밀 번호) */
    var socialUserNickName: String? = null
    var socialUserEmail: String? = null
    var uniqueValue: String? = null
    var socialUserProfileImgPath: String? = null
    var socialPath: String? = null
    var roomPassWord: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login)
        socialGoogle()  //구글 로그인
    }

    /** 구글-유저가 앱에 이미 로그인 했는지 확인 후 자동 로그인 */
    public override fun onStart() {
        super.onStart()
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleAccount !== null) { // 이미 로그인 되어있을시 바로 메인 액티비티로 이동

            val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
            uniqueValue = pref.getString("uniqueValue", "0")
            roomPassWord = pref.getString("roomPassWord", "0")
            socialUserNickName = pref.getString("name", "0")

            socialUserEmail = googleAccount.email
            socialUserProfileImgPath = googleAccount.photoUrl.toString()
            socialPath = "google"
            toMainActivity(firebaseAuth.currentUser,
                socialUserNickName!!,
                socialUserEmail!!,
                uniqueValue!!,
                socialUserProfileImgPath!!,
                pref.getString("autoAccept","")!!)
        }
    }

    /** GoogleSignInApi.getSignInIntent (...);에서 인텐트를 시작하여 반환 된 결과로 구글 로그인에 성공했을 때
     * 구글 정보(이름, 이메일, 프로필 경로), 고유 번호, 소셜 경로를 해당 변수에 값을 넣어주고,
     * firebaseAuthWithGoogle 메소드의 매개변수에 구글 로그인에 성공했다는 값을 인자에 넣어주어 Firebase.로 연동해주고,
     * 실패했을 경우 구글 로그인 실패 했다는 로그를 남겨주는 메소드 */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                val time = System.nanoTime()
                val rand = Random()
                val number = rand.nextInt(100)
                uniqueValue = if (number < 10) {
                    "0$number"
                } else {
                    number.toString()
                }

                socialUserNickName = account.displayName
                socialUserEmail = account.email
                uniqueValue = "$uniqueValue${longToBase64(time)}"
                socialUserProfileImgPath = account.photoUrl.toString()
                socialPath = "google"

            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    /** 구글 로그인과 Google 로그인 옵션 구성. requestIdToken 및 Email 요청
     * 필요한 사용자 데이터를 요청하도록 Google 로그인을 구성하려면 DEFAULT_SIGN_IN 매개 변수를
     * 사용하여 GoogleSignInOptions 개체 생성 */
    fun socialGoogle() {
        btn_googleSignIn.setOnClickListener { signInGoogle() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /** 소셜 로그인-구글 버튼을 눌렀을 경우 구글 서비스(구글 로그인) 실행*/
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /** onActivityResult.에서 구글 로그인에 성공했다는 값을 받으면
     * Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증을 하고,
     * 인증에 실패하게 되면 로그인에 실패했다는 메시지를 출력 해준다.
     * 인증에 성공하게 되면 구글 사용자 이메일과 소셜 경로인 'google'을 retrofit2(HTTP)로 서버로 데이터 전송한다.
     * 서버에서 response.로 true 값을 받으면 SharedPreference(로컬)에 로그인 정보(이름, 이메일, 회의 방 고유 번호, 소셜 경로, 회의 방 비밀 번호)를
     * 저장하고, 로그인 정보를 intent.로 데이터를 넘겨주는 메소드 */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val LoginUserInfoCheck: LoginUserInfoCheck = ApiClient.getApiClient()!!.create(
                        LoginUserInfoCheck::class.java
                    )
                    val param: HashMap<String, Any> = HashMap()
                    param["email"] = socialUserEmail!!
                    param["socialPath"] = "google"
                    val call: Call<LoginUserInfoCheckData?>? =
                        LoginUserInfoCheck.loginRequest(param)
                    call!!.enqueue(object : Callback<LoginUserInfoCheckData?> {
                        override fun onResponse(
                            call: Call<LoginUserInfoCheckData?>?,
                            response: Response<LoginUserInfoCheckData?>
                        ) {
                            if (response.isSuccessful) {
                                val userInfoCheck: Boolean = response.body()!!.result
                                if (userInfoCheck) {
                                    val pref = getSharedPreferences("loginInfo", MODE_PRIVATE)
                                    val editor = pref.edit()
                                    editor.putString("name", response.body()!!.nickname)
                                    editor.putString("email", response.body()!!.email)
                                    editor.putString(
                                        "profileImgPath",
                                        response.body()!!.profileImgPath
                                    )
                                    editor.putString("uniqueValue", response.body()!!.uniqueValue)
                                    editor.putString("socialPath", response.body()!!.socialPath)
                                    editor.putString("roomPassWord", response.body()!!.roomPassWord)
                                    editor.putString("autoAccept", response.body()!!.autoAccept)
                                    Log.d("수락/거절: ",response.body()!!.autoAccept)
                                    editor.commit()
                                    roomPassWord = response.body()!!.roomPassWord
                                    toMainActivity(firebaseAuth?.currentUser,
                                        response.body()!!.nickname,
                                        response.body()!!.email,
                                        response.body()!!.uniqueValue,
                                        response.body()!!.profileImgPath,
                                        response.body()!!.autoAccept
                                    )
                                } else {
                                    toPersonalInfoAgreement()
                                    FirebaseAuth.getInstance().signOut()
                                }
                            }
                        }

                        override fun onFailure(call: Call<LoginUserInfoCheckData?>?, t: Throwable) {
                            Log.d("TAG", "onFailure: 확인 error messageL: t : " + t.toString())
                        }
                    })
                } else {
                    Snackbar.make(ConstraintLayout_social, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    /** 소셜 로그인 시 메인(회의 참가, 회의 생성, 개인정보 설정이 있는 프레그먼트)화면으로 전환 */
    fun toMainActivity(
        user: FirebaseUser?,
        userNickName: String,
        userEmail: String,
        userUniqueValue: String,
        userProfileImgPath: String,
        autoAccept:String
    ) {
        if (user != null) {
            val loginInfo = Intent(
                applicationContext, MainActivity::class.java
            )
            loginInfo.putExtra("socialUserNickName", userNickName)
            loginInfo.putExtra("socialUserEmail", userEmail)
            loginInfo.putExtra("uniqueValue", userUniqueValue)
            loginInfo.putExtra("socialUserProfileImgPath", userProfileImgPath)
            loginInfo.putExtra("socialPath", socialPath)
            loginInfo.putExtra("roomPassWord", roomPassWord)
            loginInfo.putExtra("autoAccept", autoAccept)
            startActivity(loginInfo)
            finish()
        }
    }

    /** 개인 정보 동의 화면으로 전환 */
    fun toPersonalInfoAgreement() {
        val personalInfoAgreement = Intent(this, PersonalInfoAgreement::class.java)
        personalInfoAgreement.putExtra("socialUserNickName", socialUserNickName)
        personalInfoAgreement.putExtra("socialUserEmail", socialUserEmail)
        personalInfoAgreement.putExtra("uniqueValue", uniqueValue)
        personalInfoAgreement.putExtra("socialUserProfileImgPath", socialUserProfileImgPath)
        personalInfoAgreement.putExtra("socialPath", socialPath)
        startActivity(personalInfoAgreement)
    }


    /** 회원 가입을 하게 되면 회원 유저의 고유 번호를 가입 생성 일(10진수)을 64진수로 변환해주어 8자리로 변환해주는 메소드로
     * 8자리로 변환이 된 회원 유저의 고유 번호는 이미 만들어진 회의 방에 다른 유저가 참가할 때 회의 방 비밀 번호와 함께 사용됩니다.*/
    open fun longToBase64(v: Long): String? {
        val digits = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '#', '$'
        )
        val shift = 6
        val buf = CharArray(64)
        var charPos = 64
        val radix = 1 shl shift
        val mask = (radix - 1).toLong()
        var number = v
        do {
            buf[--charPos] = digits[(number and mask).toInt()]
            number = number ushr shift
        } while (number != 0L)
        return String(buf, charPos, 64 - charPos)
    }
}