package com.example.ot.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ot.Activity.Fragment.JoinMainFragActivity
import com.example.ot.Activity.Fragment.MeetingMainFragActivity
import com.example.ot.Activity.Fragment.SettingsMainFragActivity
import com.example.ot.NPermission
import com.example.ot.R
import com.example.ot.Socket.MeetingRoomSignalingClient
import kotlinx.android.synthetic.main.activity_main.*


/**
 * 2021.03.12
 * 설명: 로그인하고 이동되는 class이며, 현재 class는 참가,새회의,셋팅 프래그먼트 화면이 존재하는 class이다
 *
 * @author kevin
 * @version 1.0, 권한받기
 * @see None
 */

class MainActivity : AppCompatActivity(), NPermission.OnPermissionResult {
    val TAG = "MainActivity"
    /*유저정보 관련*/
    var userNickName: String? = null
    var userEmail: String? = null
    var userUniqueValue: String? = null
    var userProfileImgPath: String? = null
    var userSocialPath: String? = null
    var roomPassWord: String? = null

    /**프래그먼트 관련**/
    var joinMainFragActivity = JoinMainFragActivity()
    var meetingFragActivity = MeetingMainFragActivity()
    var settingsFragActivity = SettingsMainFragActivity()

    /*권한 관련*/
    private var nPermission: NPermission = NPermission(true)
    private var isGranted: Boolean? = null

    /*방 소켓 관련*/
    var mainSignalingClient: MeetingRoomSignalingClient?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    fun getDisplayWidth(context: Context):Int{
        var width=0
        var wm:WindowManager= context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return width
    }
    /*초기값 권한설정, 유저정보, initNavigationBar()라는 프래그먼트 버튼관련 설정메소드가 있다.*/
    private fun init() {
        if (Build.VERSION.SDK_INT < 23 || isGranted == true) {
        } else {
            nPermission.requestPermission(this, android.Manifest.permission.CAMERA)
        }

        /*홈화면(윈도우)에 화면 띄우는거 권한 받기*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                //이미 받았다면
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${this.packageName}"))
                startActivity(intent) //startActivityForResult로 대체 가능
            }
        }

        if(intent.hasExtra("socialUserNickName") && intent.hasExtra("socialUserEmail")){
            userNickName = intent.getStringExtra("socialUserNickName")
            userEmail = intent.getStringExtra("socialUserEmail")
            userUniqueValue = intent.getStringExtra("uniqueValue")
            userProfileImgPath = intent.getStringExtra("socialUserProfileImgPath")
            userSocialPath=intent.getStringExtra("socialPath")
            roomPassWord=intent.getStringExtra("roomPassWord")
        }
        initNavigationBar()

        /*소켓 객체 담기 및 소켓 연결*/
//        mainSignalingClient = MeetingRoomSignalingClient.get()!!
//        mainSignalingClient!!.socketInit()
    }

    /*초기에 버튼 클릭 설정*/
    private fun initNavigationBar() {
        linearLayout_main_participant.setOnClickListener(FragmentButtonListener())
        floatingActionButton_main_meeting.setOnClickListener(FragmentButtonListener())
        linearLayout_main_settings.setOnClickListener(FragmentButtonListener())
        floatingActionButton_main_meeting.performClick()
    }

    /*fragment change button clicklistener*/
    inner class FragmentButtonListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v) {
                linearLayout_main_participant -> {
                    changeFragment(joinMainFragActivity)
                    changeSelected(true, true, false, false, false)
                }
                floatingActionButton_main_meeting -> {
                    changeFragment(meetingFragActivity)
                    changeSelected(false, false, false, false, true)
                }
                linearLayout_main_settings -> {
                    changeFragment(settingsFragActivity)
                    changeSelected(false, false, true, true, false)
                }
            }
        }
    }

    /*fragment screen change button selected onoff -> changed color*/
    private fun changeSelected(
        tv_participant: Boolean,
        iv_particiapnt: Boolean,
        tv_settings: Boolean,
        iv_settings: Boolean,
        fab_meeting: Boolean
    ) {
        textView_main_participant.isSelected = tv_participant
        imageview_main_participant.isSelected = iv_particiapnt
        textView_main_settings.isSelected = tv_settings
        imageView_main_settings.isSelected = iv_settings
        floatingActionButton_main_meeting.isSelected = fab_meeting
    }

    /**
     * 프래그먼트를 변경해주는 메소드 -> 바텀네비게이션 버튼을 클릭할 때마다 프래그먼트 변경해준다.
     * 프래그먼트 변경해주면서 유저 정보도 같이 넘겨준다.
     * */
    private fun changeFragment(fragment: Fragment) {
        var bundle = Bundle()
        bundle.putString("userEmail", userEmail)
        bundle.putString("userNickName", userNickName)
        bundle.putString("userUniqueValue", userUniqueValue)
        bundle.putString("userProfileImgPath", userProfileImgPath)
        bundle.putString("userSocialPath", userSocialPath)
        bundle.putString("roomPassWord", roomPassWord)
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(permission: String?, isGranted: Boolean) {
        when (permission) {
            android.Manifest.permission.CAMERA -> {
                this.isGranted = isGranted
                if (!isGranted) {
                    nPermission.requestPermission(this, android.Manifest.permission.CAMERA)
                } else {
                    nPermission.requestPermission(this, android.Manifest.permission.RECORD_AUDIO)
                }
            }
            else -> {
            }
        }
    }
}