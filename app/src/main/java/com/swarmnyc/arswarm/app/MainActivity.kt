package com.swarmnyc.arswarm.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.swarmnyc.arswarm.BuildConfig
import com.swarmnyc.arswarm.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    var requiredPermissions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ensurePermissions()

        btn_host.setOnClickListener {
            startActivity(Intent(this, HostActivity::class.java))
        }

        btn_guest.setOnClickListener {
            startActivity(Intent(this, GuestActivity::class.java))
        }
    }

    private fun ensurePermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.CAMERA)
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (requiredPermissions.size > 0) {
            requestPermissions(requiredPermissions.toTypedArray(), 9999)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 9999) {
            permissions.forEachIndexed { index, s ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    requiredPermissions.remove(s)
                }
            }

            if (requiredPermissions.size > 0) {
                AlertDialog.Builder(this)
                        .setTitle("WARNING")
                        .setMessage("No Permission")
                        .setPositiveButton("Go to settings") { dialog, which ->
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${BuildConfig.APPLICATION_ID}")))
                        }
                        .create()
                        .show()
            }
        }
    }
}

