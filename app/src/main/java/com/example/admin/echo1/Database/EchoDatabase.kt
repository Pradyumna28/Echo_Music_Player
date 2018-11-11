package com.example.admin.echo1.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.admin.echo1.Song

class EchoDatabase : SQLiteOpenHelper {


    object StaticFields {
        val dbName = "FavouriteDatabase"
        val tableName = "FavouriteTable"
        val columnID = "SongID"
        val columnTitle = "SongTitle"
        val columnArtist = "SongArtist"
        val columnPath = "SongPath"
        var dbVersion = 1
    }

    var songList = ArrayList<Song>()

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)

    constructor(context: Context?) : super(context, StaticFields.dbName, null, StaticFields.dbVersion)


    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE " + StaticFields.tableName + "(" + StaticFields.columnID + " INTEGER,"
                + StaticFields.columnTitle + " STRING," + StaticFields.columnArtist + " STRING," + StaticFields.columnPath + " STRING);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun storeAsFavourites(id: Int?, title: String?, artist: String?, path: String?) {

        val db = this.writableDatabase
        val contentValues = ContentValues()



        contentValues.put(StaticFields.columnID, id)
        contentValues.put(StaticFields.columnTitle, title)
        contentValues.put(StaticFields.columnArtist, artist)
        contentValues.put(StaticFields.columnPath, path)
        db.insert(StaticFields.tableName, null, contentValues)
        db.close()

    }

    fun queryDBList(): ArrayList<Song>? {


        try {
            val db = this.readableDatabase
            val query = "SELECT * FROM " + StaticFields.tableName
            var cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {


                do {
                    val _id = cursor.getInt(cursor.getColumnIndexOrThrow(StaticFields.columnID))
                    val _title = cursor.getString(cursor.getColumnIndexOrThrow(StaticFields.columnTitle))
                    val _artist = cursor.getString(cursor.getColumnIndexOrThrow(StaticFields.columnArtist))
                    val _path = cursor.getString(cursor.getColumnIndexOrThrow(StaticFields.columnPath))

                    songList.add(Song(_id.toLong(), _title, _artist, _path, 0))
                } while (cursor.moveToNext())
            } else
                return null


        } catch (e: Exception) {
            e.printStackTrace()
        }



        return songList


    }


    fun checkIfIDExists(id: Int): Boolean {

        var check = -1090
        val db = this.writableDatabase
        val query = "SELECT * FROM " + StaticFields.tableName + " where " +
                StaticFields.columnID + " = '$id'"
        var cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                check = cursor.getInt(cursor.getColumnIndexOrThrow(StaticFields.columnID))

            } while (cursor.moveToNext())
        } else
            return false
        return check != -1090
    }


    fun deleteFavourite(id: Int) {

        val db = this.writableDatabase

        db.delete(StaticFields.tableName, StaticFields.columnID + "=" + id,
                null)
        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        val query = "select * from " + StaticFields.tableName
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                counter += 1
            } while (cursor.moveToNext())
        } else {
            return 0
        }
        return counter
    }


}