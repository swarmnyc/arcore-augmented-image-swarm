package com.swarmnyc.arswarm.app

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.swarmnyc.arswarm.BuildConfig

/**
 * for check permissions and load resource
 */
abstract class ArBaseActivity : AppCompatActivity() {
    companion object {
        private const val RequestCodeARCode = 100
        private const val RequestCodePermission = 200
    }

    private var askTime = 0
    private val requiredPermissions = mutableMapOf(Manifest.permission.CAMERA to "camera")

    protected abstract val viewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewId)

        checkArCore()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.remove(permission)
            }
        }

        askPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCodeARCode -> checkArCore()
            RequestCodePermission -> checkPermissions()
        }
    }

    private fun checkPermissions() {
        requiredPermissions.forEach {
            // require CAMERA
            if (ContextCompat.checkSelfPermission(this, it.key) == PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.remove(it.key)
            }
        }

        askPermissions()
    }

    private fun askPermissions() {
        askTime++

        if (requiredPermissions.isEmpty()) {
            startAr()
        } else {
            if (askTime > 2 || ActivityCompat.shouldShowRequestPermissionRationale(this, requiredPermissions.keys.first())) {
                alertPermissions()
            } else {
                ActivityCompat.requestPermissions(this, requiredPermissions.keys.toTypedArray(), RequestCodePermission)
            }
        }
    }

    private fun alertPermissions() {
        val names = requiredPermissions.values.joinToString(", ")
        val s = if (requiredPermissions.size == 1) {
            ""
        } else {
            "s"
        }

        AlertDialog.Builder(this)
                .setTitle("Require Permissions")
                .setMessage("This app needs $names permission$s to work.")
                .setPositiveButton("Go to settings") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${BuildConfig.APPLICATION_ID}")), RequestCodePermission)
                }
                .setNegativeButton("Close App") { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .show()
    }

    private fun checkArCore() {
        val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion

        val arcoreInstalled = try {
            packageManager.getPackageInfo("com.google.ar.core", PackageManager.GET_SERVICES)
            true
        } catch (e: Throwable) {
            false
        }

        if (!arcoreInstalled) {
            AlertDialog.Builder(this)
                    .setTitle("Requirements")
                    .setMessage("This app needs AR Core installed.")
                    .setPositiveButton("Go to Play Store") { _, _ ->
                        startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core")), RequestCodeARCode)
                    }
                    .setNegativeButton("Close App") { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
        } else if (java.lang.Double.parseDouble(openGlVersionString) < 3) {
            AlertDialog.Builder(this)
                    .setTitle("Requirements")
                    .setMessage("This app needs OpenGL ES 3.0 or later to work.")
                    .setPositiveButton("Close App") { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
        } else {
            checkPermissions()
        }
    }

    abstract fun startAr()
}
