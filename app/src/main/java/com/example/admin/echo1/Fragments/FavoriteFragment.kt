package com.example.admin.echo1.Fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echo1.Adapters.FavouritesAdapter
import com.example.admin.echo1.Database.EchoDatabase
import com.example.admin.echo1.R
import com.example.admin.echo1.Song


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {

    var myActivity: Activity? = null
    var noFav: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var favouriteContent: EchoDatabase? = null
    var trackPosition: Int = 0
    var refreshList: ArrayList<Song>? = null
    var getListFromDatabase: ArrayList<Song>? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity.title = "Favourites"
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        noFav = view?.findViewById(R.id.no_favourites_textview)
        nowPlayingBottomBar = view.findViewById(R.id.hidden_bar_favscreen)
        songTitle = view.findViewById(R.id.song_title_fav_screen)
        playPauseButton = view.findViewById(R.id.play_pause_button)
        recyclerView = view.findViewById(R.id.favourite_recyclerview)
        favouriteContent = EchoDatabase(myActivity)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        displayFavouritesBySearching()
        bottomBarSetup()
    }

    private fun getSongsFromPhone(): ArrayList<Song>? {

        var arrayList = ArrayList<Song>()
        var contentResolver = myActivity?.contentResolver

        var songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var songCursor = contentResolver?.query(songURI, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentID = songCursor.getLong(songID)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)



                arrayList.add(Song(currentID, currentTitle, currentArtist, currentData, currentDate))

            }


        }

        return arrayList
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


    fun bottomBarSetup() {


        try {
            bottomBarClickHandler()
            songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            }

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
                nowPlayingBottomBar?.visibility = View.VISIBLE
            else
                nowPlayingBottomBar?.visibility = View.INVISIBLE


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun bottomBarClickHandler() {

        nowPlayingBottomBar?.setOnClickListener {


            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer

            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()

            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("songPath", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songID", SongPlayingFragment.Statified.currentSongHelper?.songID?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "Success")

            songPlayingFragment.arguments = args

            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        }


        playPauseButton?.setOnClickListener {

            if (SongPlayingFragment.Statified.mediaPlayer!!.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer!!.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

    }

    fun displayFavouritesBySearching() {

        if (favouriteContent?.checkSize() as Int > 0) {

            refreshList = ArrayList<Song>()
            getListFromDatabase = favouriteContent?.queryDBList() as ArrayList<Song>


            var fetchListFromDevice = getSongsFromPhone()



            if (true) {
                for (i in 0 until fetchListFromDevice!!.size) {
                    for (j in 0 until getListFromDatabase!!.size) {
                        if ((getListFromDatabase!![j].songID) == (fetchListFromDevice[i].songID)) {
                            refreshList?.add((getListFromDatabase as ArrayList<Song>)[j])
                        }
                    }
                }
            } else {

            }

            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFav?.visibility = View.VISIBLE
            } else {
                var favouritesAdapter = FavouritesAdapter(refreshList as ArrayList<Song>, myActivity as Context)
                val layoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = layoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favouritesAdapter
                recyclerView?.setHasFixedSize(true)
            }

        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFav?.visibility = View.VISIBLE
        }
    }

}
