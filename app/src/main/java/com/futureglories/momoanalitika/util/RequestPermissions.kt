package com.futureglories.momoanalitika.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object RequestPermissions {
    fun request (activity: Activity)
    {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        } else {
            // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$UssdCodeNew")))
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_SMS),
                1
            )
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                1
            )
        }
    }
}