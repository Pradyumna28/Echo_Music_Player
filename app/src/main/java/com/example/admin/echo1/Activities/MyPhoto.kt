package com.example.admin.echo1.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.admin.echo1.R

class MyPhoto : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_photo)

        this.title = "Profile Picture"
    }


}
