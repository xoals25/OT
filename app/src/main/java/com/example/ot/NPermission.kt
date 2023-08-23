package com.example.ot

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.reflect.Method

/**
 * 권한을 얻어오기 위한 클래스
 *
 * @author kevin
 * @version 1.0
 * @see None
 */
class NPermission @JvmOverloads constructor(var isForceRequest: Boolean = false) {

//    companion object {
//        private val TAG = NPermission::class.java.simpleName
//    }

    /**
     * Request code for all permissions
     */
    private val N_PERMISSIONS_REQUEST = 0x007

    /**
     * Activity reference for reflection
     */
    private var runningActivity: Activity? = null
    private var neverAskFlag = false

    /**
     * Go to application setting
     */
    fun startInstalledAppDetailsActivity(context: Activity?) {
        if (context == null) {
            return
        }
        neverAskFlag = true
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    fun checkPermissionGranted(permission: String?): Boolean {
        return (ContextCompat.checkSelfPermission(runningActivity!!.applicationContext,
                permission!!)
                == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * method to request permission
     *
     * @param runningActivity current activity reference
     * @param permission permission to ask
     */
    fun requestPermission(runningActivity: Activity, permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.runningActivity = runningActivity
            if (ContextCompat.checkSelfPermission(runningActivity.applicationContext,
                            permission) != PackageManager.PERMISSION_GRANTED && !neverAskFlag) {
                ActivityCompat.requestPermissions(runningActivity, arrayOf(permission), N_PERMISSIONS_REQUEST)
            } else {
                callInterface(runningActivity, permission, checkPermissionGranted(permission))
            }
        }
    }

    /**
     * This method is called in onRequestPermissionsResult of the runningActivity
     *
     * @param requestCode The request code of runningActivity.
     * @param permissions The requested permissions of runningActivity.
     * @param grantResults The grant results for the corresponding permissions from runningActivity
     */
    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        try {
            when (requestCode) {
                N_PERMISSIONS_REQUEST -> {
                    if (grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (runningActivity != null) {
                            callInterface(runningActivity!!, permissions[0], true)
                        }
                    } else {
                        if (runningActivity != null) {
                            callInterface(runningActivity!!, permissions[0], false)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Method to call OnPermissionResult interface
     *
     * @param activity activity reference
     * @param permission current asked permission
     * @param isGranted true if permission granted false otherwise
     * @throws InterfaceNotImplementedException throws when OnPermissionResult is not implemented
     */
    @Throws(InterfaceNotImplementedException::class)
    private fun callInterface(activity: Activity, permission: String, isGranted: Boolean) {
        val method: Method?
        method = try {
            activity.javaClass.getMethod("onPermissionResult", String::class.java, Boolean::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            throw InterfaceNotImplementedException(
                    "please implement NPermission.OnPermissionResult interface in your activity to get the permissions result")
        }
        if (method != null) {
            try {
                if (isForceRequest && !isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.shouldShowRequestPermissionRationale(permission)) {
                        requestPermission(activity, permission)
                    } else {
                        startInstalledAppDetailsActivity(activity)
                    }
                } else {
                    method.invoke(activity, permission, isGranted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Interface to notify permission result
     */
    interface OnPermissionResult {
        /**
         * Method will get called after permission request
         *
         * @param permission asked permission
         * @param isGranted true if permission granted false otherwise
         */
        fun onPermissionResult(permission: String?, isGranted: Boolean)
    }

    /**
     * Exception throws when OnPermissionResult interface not implemented
     */
    private inner class InterfaceNotImplementedException(message: String) : RuntimeException(message)

}
