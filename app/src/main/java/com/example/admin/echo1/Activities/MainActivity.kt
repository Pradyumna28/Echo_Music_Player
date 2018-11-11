package com.example.admin.echo1.Activities

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.admin.echo1.Activities.MainActivity.Statified.mNotificationManager
import com.example.admin.echo1.Adapters.NavigationDrawerAdapter
import com.example.admin.echo1.Fragments.MainScreenFragment
import com.example.admin.echo1.Fragments.SongPlayingFragment
import com.example.admin.echo1.R

class MainActivity : AppCompatActivity() {

    private var iconNavdrawer = intArrayOf(R.drawable.navigation_allsongs,
            R.drawable.navigation_favorites, R.drawable.navigation_settings,
            R.drawable.navigation_aboutus)
    private var textNavdrawer: ArrayList<String> = arrayListOf()
    private var notification: Notification? = null


    @SuppressLint("StaticFieldLeak")
    object Statified {
        var drawerLayout: DrawerLayout? = null
        var mNotificationManager: NotificationManager? = null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        textNavdrawer.add("All Songs")
        textNavdrawer.add("Favorites")
        textNavdrawer.add("Settings")
        textNavdrawer.add("About us")

        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(this@MainActivity,
                MainActivity.Statified.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        MainActivity.Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = MainScreenFragment()

        this.supportFragmentManager.beginTransaction()
                .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
                .commit()


        val navigationDrawerAdapter = NavigationDrawerAdapter(textNavdrawer, iconNavdrawer,
                this)
        navigationDrawerAdapter.notifyDataSetChanged()

        val recyclerView = findViewById<RecyclerView>(R.id.navigationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = navigationDrawerAdapter
        recyclerView.setHasFixedSize(true)


        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this@MainActivity,
                System.currentTimeMillis().toInt(), intent, 0)


        val channelId = "ECHO"
        val name = "Echo"
        var importance = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT
        }
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager?.createNotificationChannel(
                    NotificationChannel(channelId, name, importance))
        }

        notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.echo_notif_icon)
                .setContentTitle("A track is playing in the background")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build()
    }

    override fun onStart() {
        super.onStart()

        try {
            mNotificationManager?.cancel(1998)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()

        try {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                mNotificationManager?.notify(1998, notification)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()

        try {
            mNotificationManager?.cancel(1998)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
