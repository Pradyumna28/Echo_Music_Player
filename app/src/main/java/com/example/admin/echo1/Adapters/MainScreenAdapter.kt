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
import com.example.admin.echo1.Fragments.MainScreenFragment
import com.example.admin.echo1.Fragments.SongPlayingFragment
import com.example.admin.echo1.R
import com.example.admin.echo1.Song

class MainScreenAdapter(songList: ArrayList<Song>, _context: Context) : RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Song>? = null
    var mContext: Context? = null

    init {
        this.mContext = _context
        this.songDetails = songList
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainScreenAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails == null)
            return 0
        else
            return (songDetails as ArrayList<Song>).size

    }

    override fun onBindViewHolder(holder: MainScreenAdapter.MyViewHolder?, position: Int) {
        val songObject = songDetails?.get(position)

        var songName: String? = songObject?.songTitle
        var singer: String? = songObject?.artist

        if (songName.equals("<unknown>", true))
            songName = "Unknown"
        if (singer.equals("<unknown>", true))
            singer = "Unknown"

        holder?.trackTitle?.text = songName
        holder?.trackArtist?.text = singer
        holder?.contentHolder?.setOnClickListener {

            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()

            MainScreenFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer

            println("song id on click " + songObject?.songID?.toInt())

            args.putString("songArtist", songObject?.artist)
            args.putString("songPath", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songID", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragment.arguments = args



            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = itemView.findViewById(R.id.track_title) as TextView
            trackArtist = itemView.findViewById(R.id.track_artist) as TextView
            contentHolder = itemView.findViewById(R.id.content_row) as RelativeLayout
        }
    }
}