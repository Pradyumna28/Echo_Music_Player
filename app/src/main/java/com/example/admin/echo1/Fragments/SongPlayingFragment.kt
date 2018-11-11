package com.example.admin.echo1.Fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.admin.echo1.CurrentSongHelper
import com.example.admin.echo1.Database.EchoDatabase
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.mAcceleration
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.mAccelerationCurrent
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.mAccelerationLast
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.playNext
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.processInformation
import com.example.admin.echo1.Fragments.SongPlayingFragment.Staticated.updateTextView
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.audioVisualization
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.currentPosition
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.currentSongHelper
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.endTimeText
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.fab
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.favouriteContent
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.glView
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.loopImageButton
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.mediaPlayer
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.myActivity
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.nextImageButton
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.previousImageButton
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.seekBar
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.songArtistText
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.songTitleText
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.startTimeText
import com.example.admin.echo1.Fragments.SongPlayingFragment.Statified.updateSongTime
import com.example.admin.echo1.R
import com.example.admin.echo1.Song
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {


    @SuppressLint("StaticFieldLeak")
    object Statified {

        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistText: TextView? = null
        var songTitleText: TextView? = null
        var fab: ImageButton? = null
        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Song>? = null
        var favouriteContent: EchoDatabase? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var updateSongTime = object : Runnable {
            override fun run() {


                val getCurrent = mediaPlayer?.currentPosition

                var minutes = TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long)
                var seconds = TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes
                        (getCurrent?.toLong()))


                var newSec: String

                newSec = if (seconds <= 9) "0$seconds" else seconds.toString()

                startTimeText?.text = String.format("%s:%s", minutes, newSec)
                seekBar?.progress = getCurrent
                Handler().postDelayed(this, 1000)
            }

        }

    }


    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle Feature"
        var MY_PREFS_LOOP = "Loop Feature"

        var mAcceleration: Float = 0f
        var mAccelerationCurrent: Float = 0f
        var mAccelerationLast: Float = 0f

        fun playNext(check: String) {


            if (check.equals("PLAY_NEXT_NORMAL", true)) {
                Statified.currentPosition += 1
            } else if (check.equals("PLAY_NEXT_LIKE_NORMAL_SHUFFLE", true)) {
                var random = Random()
                var temp = random.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = temp
            }

            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isloop = false


            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.currentPosition = currentPosition
            currentSongHelper?.songID = nextSong?.songID as Long
            updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()

            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favouriteContent?.checkIfIDExists(currentSongHelper?.songID?.toInt() as Int) as Boolean) {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }

        }

        fun onSongComplete() {


            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PLAY_NEXT_LIKE_NORMAL_SHUFFLE")
                currentSongHelper?.isPlaying = true
            } else {
                if (currentSongHelper?.isloop as Boolean) {

                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songID = nextSong?.songID as Long
                    updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
                    mediaPlayer?.reset()
                    try {
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)
                        MainScreenFragment().bottomBarSetup()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PLAY_NEXT_NORMAL")
                    currentSongHelper?.isPlaying = true
                }
            }

            if (favouriteContent?.checkIfIDExists(currentSongHelper?.songID?.toInt() as Int) as Boolean) {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }


        }

        fun updateTextView(songTitle: String, songArtist: String) {

            var songName: String? = songTitle
            var singer: String = songArtist
            if (songName.equals("<unknown>", true))
                songName = "Unknown"
            if (singer.equals("<unknown>", true))
                singer = "Unknown"
            songTitleText?.setText(songName)
            songArtistText?.setText(singer)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar?.max = finalTime

            startTimeText?.text = String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))

            var seconds = TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))


            var newSec: String

            newSec = if (seconds <= 9) "0$seconds" else seconds.toString()

            endTimeText?.text = String.format("%s:%s",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()), newSec
            )

            seekBar?.progress = startTime
            Handler().postDelayed(updateSongTime, 1000)
        }


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        activity.title = "Now Playing"
        val v: View? = inflater.inflate(R.layout.fragment_song_playing, container,
                false)
        startTimeText = v?.findViewById(R.id.start_time)
        endTimeText = v?.findViewById(R.id.end_time)
        playPauseImageButton = v?.findViewById(R.id.play_pause_button_songplaying)
        previousImageButton = v?.findViewById(R.id.previous_button)
        nextImageButton = v?.findViewById(R.id.next_button)
        loopImageButton = v?.findViewById(R.id.loop_button)
        shuffleImageButton = v?.findViewById(R.id.shuffle_button)
        seekBar = v?.findViewById(R.id.seekbar)
        songArtistText = v?.findViewById(R.id.song_artist)
        songTitleText = v?.findViewById(R.id.song_title)
        glView = v?.findViewById(R.id.visualizer_view)
        fab = v?.findViewById(R.id.favouriteIcon)
        return v

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioVisualization = glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = false
        currentSongHelper?.isloop = false
        currentSongHelper?.isShuffle = false




        favouriteContent = EchoDatabase(myActivity)

        val vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context,
                0)
        audioVisualization?.linkTo(vizualizerHandler)

        var path: String?
        var songTitle: String?
        var songArtist: String?
        var songID: Long

        try {
            path = arguments.getString("songPath")
            songTitle = arguments.getString("songTitle")
            songArtist = arguments.getString("songArtist")
            songID = arguments.getInt("songID").toLong()
            currentPosition = arguments.getInt("songPosition")
            fetchSongs = arguments.getParcelableArrayList("songData")



            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = songTitle
            currentSongHelper?.songArtist = songArtist
            currentSongHelper?.songID = songID
            currentSongHelper?.currentPosition = currentPosition


            updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)


        } catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments.get("FavBottomBar") as? String
        var fromMainBottomBar = arguments.get("MainScreenBottomBar") as? String

        if (!fromFavBottomBar.equals(null)) {
            mediaPlayer = FavoriteFragment.Statified.mediaPlayer
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        } else if (!fromMainBottomBar.equals(null)) {
            mediaPlayer = MainScreenFragment.Statified.mediaPlayer
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        } else {
            if (!(currentSongHelper?.isPlaying as Boolean)) {
                mediaPlayer?.reset()
                mediaPlayer = MediaPlayer()
            }
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mediaPlayer?.start()

        }

        currentSongHelper?.isPlaying = true



        processInformation(mediaPlayer as MediaPlayer)

        if (currentSongHelper?.isPlaying as Boolean) {

            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        mediaPlayer?.setOnCompletionListener {
            onSongComplete()
        }

        clickHandler()

        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isloop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isloop = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isloop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        if (favouriteContent?.checkIfIDExists(currentSongHelper?.songID?.toInt() as Int) as Boolean) {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }


        })


    }


    fun playPrevious() {

        currentPosition -= 1

        if (currentPosition == -1) {
            currentPosition = 0
        }

        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        currentSongHelper?.isloop = false
        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songID = nextSong?.songID as Long
        updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        mediaPlayer?.reset()

        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformation(mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favouriteContent?.checkIfIDExists(currentSongHelper?.songID?.toInt() as Int) as Boolean) {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }


    }

    fun clickHandler() {


        fab?.setOnClickListener {
            if (favouriteContent?.checkIfIDExists(currentSongHelper?.songID?.toInt() as Int) as Boolean) {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
                favouriteContent?.deleteFavourite(currentSongHelper?.songID?.toInt() as Int)
                Toast.makeText(myActivity, "Removed from favourites!", Toast.LENGTH_SHORT).show()
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))


                favouriteContent?.storeAsFavourites(currentSongHelper?.songID?.toInt() as Int,
                        currentSongHelper?.songTitle, currentSongHelper?.songArtist,
                        currentSongHelper?.songPath)
                Toast.makeText(myActivity, "Added to favourites!", Toast.LENGTH_SHORT).show()
            }
        }

        shuffleImageButton?.setOnClickListener {
            var editorShuffle = myActivity?.getSharedPreferences(SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(SongPlayingFragment.Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()


            if (currentSongHelper?.isShuffle as Boolean) {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isloop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        }


        nextImageButton?.setOnClickListener {

            currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if ((currentSongHelper?.isShuffle) as Boolean) {
                playNext("PLAY_NEXT_LIKE_NORMAL_SHUFFLE")
            } else {
                playNext("PLAY_NEXT_NORMAL")
            }
        }

        previousImageButton?.setOnClickListener {

            currentSongHelper?.isPlaying = true

            if (currentSongHelper?.isloop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }

            playPrevious()
        }

        loopImageButton?.setOnClickListener {
            var editorShuffle = myActivity
                    ?.getSharedPreferences(SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity
                    ?.getSharedPreferences(SongPlayingFragment.Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (currentSongHelper?.isloop as Boolean) {
                currentSongHelper?.isloop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()

            } else {
                currentSongHelper?.isloop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        }


        playPauseImageButton?.setOnClickListener {

            if (currentSongHelper?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

    }


    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        audioVisualization?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    fun bindShakeListener() {

        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                mAccelerationLast = mAccelerationCurrent

                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()

                val delta = mAccelerationCurrent - mAccelerationLast

                mAcceleration = mAcceleration * 0.9f + delta


                if (mAcceleration > 12) {

                    val prefs = Statified.myActivity
                            ?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        SongPlayingFragment.Staticated.playNext("PLAY_NEXT_NORMAL")
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true

        val item2 = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false


    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }
}




