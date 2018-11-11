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
import com.example.admin.echo1.Adapters.MainScreenAdapter
import com.example.admin.echo1.R
import com.example.admin.echo1.Song
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainScreenFragment : Fragment() {

    var getSongsList: ArrayList<Song>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null
    var trackPosition: Int = 0


    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getSongsList = getSongsFromPhone()
        val prefs = activity.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val actionSortAscending = prefs.getString("action_sort_ascending", "true")
        val actionSortRecent = prefs.getString("action_sort_recent", "false")

        if (getSongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Song>, myActivity as Context)
            val layoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = layoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }

        if (getSongsList != null) {
            if (actionSortAscending.equals("true", true)) {
                Collections.sort(getSongsList, Song.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (actionSortRecent.equals("true", false)) {
                Collections.sort(getSongsList, Song.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }


        bottomBarSetup()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_main_screen, container, false)
        nowPlayingBottomBar = view.findViewById(R.id.hidden_bar_mainscreen)
        playPauseButton = view.findViewById(R.id.play_pause_button)
        songTitle = view.findViewById(R.id.song_title_main_screen)
        visibleLayout = view.findViewById(R.id.visible_layout)
        noSongs = view.findViewById(R.id.no_songs)
        recyclerView = view.findViewById(R.id.contentMain)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    fun getSongsFromPhone(): ArrayList<Song> {

        var songList = ArrayList<Song>()
        val contentResolver = myActivity?.contentResolver
        val songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val songcursor = contentResolver?.query(songURI, null, null,
                null, null)

        if (songcursor != null && songcursor.moveToFirst()) {
            var songIdIndex = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songTitleIndex = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var songArtistIndex = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var songDataIndex = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var songDateIndex = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songcursor.moveToNext()) {
                songList.add(Song(songcursor.getLong(songIdIndex),
                        songcursor.getString(songTitleIndex),
                        songcursor.getString(songArtistIndex),
                        songcursor.getString(songDataIndex),
                        songcursor.getLong(songDateIndex)))
            }


        }
        return songList
    }

    fun bottomBarSetup() {

        bottomBarClickHandler()

        try {

            songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            }

            if (SongPlayingFragment.Statified.mediaPlayer!!.isPlaying as Boolean)
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
            val args = Bundle()

            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("songPath", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songID", SongPlayingFragment.Statified.currentSongHelper?.songID?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("MainScreenBottomBar", "Success")

            songPlayingFragment.arguments = args

            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        }


        playPauseButton?.setOnClickListener {

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.action_sort_ascending -> {
                val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                editor?.putString("action_sort_ascending", "true")
                editor?.putString("action_sort_recent", "false")
                editor?.apply()
                if (getSongsList != null) {
                    Collections.sort(getSongsList, Song.Statified.nameComparator)
                }
                _mainScreenAdapter?.notifyDataSetChanged()
                return false
            }

            R.id.action_sort_recent -> {
                val editor = myActivity
                        ?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                editor?.putString("action_sort_ascending", "false")
                editor?.putString("action_sort_recent", "true")
                editor?.apply()
                if (getSongsList != null) {
                    Collections.sort(getSongsList, Song.Statified.dateComparator)
                }
                _mainScreenAdapter?.notifyDataSetChanged()

                return false
            }

        }
        return false
    }


}
