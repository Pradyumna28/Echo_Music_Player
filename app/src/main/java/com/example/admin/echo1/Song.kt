package com.example.admin.echo1

import android.os.Parcel
import android.os.Parcelable

class Song(var songID: Long, var songTitle: String, var artist: String, var songData: String, var dateAdded: Long) : Parcelable {

    object Statified {
        var nameComparator: Comparator<Song> = Comparator<Song> { song1, song2 ->

            val S1 = song1.songTitle?.toUpperCase()
            val S2 = song2.songTitle?.toUpperCase()
            S1.compareTo(S2)
        }

        var dateComparator: Comparator<Song> = Comparator<Song> { song1, song2 ->
            val S1 = song1.dateAdded.toDouble()
            val S2 = song2.dateAdded.toDouble()

            S2.compareTo(S1)

        }
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

}


