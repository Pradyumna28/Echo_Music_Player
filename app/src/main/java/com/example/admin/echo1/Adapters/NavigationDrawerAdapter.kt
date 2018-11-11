package com.example.admin.echo1.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echo1.Activities.MainActivity
import com.example.admin.echo1.Fragments.AboutUsFragment
import com.example.admin.echo1.Fragments.FavoriteFragment
import com.example.admin.echo1.Fragments.MainScreenFragment
import com.example.admin.echo1.Fragments.SettingFragment
import com.example.admin.echo1.R

class NavigationDrawerAdapter(_contentList: ArrayList<String>?, _contentImages: IntArray, _context: Context) : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {

    private var contentList: ArrayList<String>? = null
    private var contentImages: IntArray? = null
    private var context: Context? = null

    init {
        contentList = _contentList
        contentImages = _contentImages
        context = _context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_navigation_drawer, parent, false)

        return NavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {


        holder.text_navdrawer?.text = contentList?.get(position)
        holder.icon_navdrawer?.setBackgroundResource(contentImages?.get(position) as Int)
        holder.contentHolder?.setOnClickListener {
            when (position) {
                0 -> {
                    val mainScreenFragment = MainScreenFragment()
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, mainScreenFragment)
                            .commit()
                }
                1 -> {
                    val favoriteFragment = FavoriteFragment()
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, favoriteFragment)
                            .commit()
                }
                2 -> {
                    val settingFragment = SettingFragment()
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, settingFragment)
                            .commit()
                }
                else -> {
                    val aboutusFragment = AboutUsFragment()
                    (context as MainActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, aboutusFragment)
                            .commit()
                }
            }

            MainActivity.Statified.drawerLayout?.closeDrawers()


        }
    }


    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var icon_navdrawer: ImageView? = null
        var text_navdrawer: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            icon_navdrawer = itemView?.findViewById(R.id.icon_navdrawer)
            text_navdrawer = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.nav_drawer_content_holder)
        }


    }

}