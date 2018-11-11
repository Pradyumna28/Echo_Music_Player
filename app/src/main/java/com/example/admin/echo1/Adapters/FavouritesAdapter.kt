package com.example.admin.echo1.Adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echo1.Fragments.SongPlayingFragment
import com.example.admin.echo1.R
import com.example.admin.echo1.Song

class FavouritesAdapter(songDetails: ArrayList<Song>, context: Context) : RecyclerView.Adapter<FavouritesAdapter.MyViewHolder>() {

    var songList: ArrayList<Song>? = null
    var context: Context? = null

    init {
        songList = songDetails
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {


        val item = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)

        return MyViewHolder(item)


    }

    override fun getItemCount(): Int {
        return songList?.size as Int


    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val songObject = songList?.get(position)
        holder?.trackArtist?.text = songObject?.artist
        holder?.trackTitle?.text = songObject?.songTitle
        holder?.contentHolder?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()

            args.putString("songArtist", songObject?.artist)
            args.putString("songTitle", songObject?.songTitle)
            args.putString("songPath", songObject?.songData)
            args.putInt("songID", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songList)

            songPlayingFragment.arguments = args

            (context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragmentFavourite")
                    .commit()
        }


    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = itemView?.findViewById(R.id.track_title) as TextView
            trackArtist = itemView.findViewById(R.id.track_artist) as TextView
            contentHolder = itemView.findViewById(R.id.content_row) as RelativeLayout
        }


    }

}