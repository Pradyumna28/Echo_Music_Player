package com.example.admin.echo1.Utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.admin.echo1.Activities.MainActivity
import com.example.admin.echo1.Fragments.SongPlayingFragment
import com.example.admin.echo1.R

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        println("OnRecieve")


        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.mNotificationManager?.cancel(1998)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

            when (tm.callState) {

                TelephonyManager.CALL_STATE_RINGING -> {
                   try {
                        MainActivity.Statified.mNotificationManager?.cancel(1998)

                    if (SongPlayingFragment.Statified.mediaPlayer!!.isPlaying as Boolean) {
                        println("Ringing")
                        SongPlayingFragment.Statified.mediaPlayer!!.pause()
                        SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                    }
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }
                }


            }
        }

    }


}
