package com.example.admin.echo1.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.admin.echo1.R

class SplashActivity : AppCompatActivity() {

    var permissionString = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.PROCESS_OUTGOING_CALLS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!hasPermission(this@SplashActivity, *permissionString))
            ActivityCompat.requestPermissions(this@SplashActivity, permissionString,
                    69)
        else
            gotoMainActivity()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            69 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                    gotoMainActivity()
                    return
                } else {
                    Toast.makeText(this, "Please give all the permissions",
                            Toast.LENGTH_SHORT).show()
                    this.finish()
                    return
                }
            }
            else -> {
                Toast.makeText(this, "Something happened", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }
    }

    private fun gotoMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }, 1000)

    }

    private fun hasPermission(context: Context, vararg permissions: String): Boolean {

        var hasPerm = true

        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED)
                hasPerm = false
        }

        return hasPerm
    }
}
