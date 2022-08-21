package com.example.happyplaces.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ) {

    companion object{
        private const val DATABASE_NAME = "HappyPLacesDatabase"
        private const val DATABASE_VERSION =1
        private const val  TABLE_CONTACTS = "HappyPlacesTable"

        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE  =  "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE =  "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }
    fun addHappyPlaces(happyPlace:HappyPlaceModel): Long{
        val db = this.writableDatabase

        val contentValues =  ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put( KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        val result = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()

        return result
    }



    fun getHappyPlacesList():ArrayList<HappyPlaceModel>{
        val happyPlaceList = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase

        try {
            val cursor :Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()){
                do {
                        val hehe = cursor.getColumnIndex(KEY_ID)
                    val hehe1 = cursor.getColumnIndex(KEY_TITLE)
                    val hehe2 = cursor.getColumnIndex(KEY_IMAGE)
                    val hehe3 = cursor.getColumnIndex(KEY_DESCRIPTION)
                    val hehe4 = cursor.getColumnIndex(KEY_DATE)
                    val hehe5 = cursor.getColumnIndex(KEY_LOCATION)
                    val hehe6 = cursor.getColumnIndex(KEY_LATITUDE)
                    val hehe7 = cursor.getColumnIndex(KEY_LONGITUDE)
                    val place = HappyPlaceModel(
                        cursor.getInt(hehe),
                        cursor.getString(hehe1),
                        cursor.getString(hehe2),
                        cursor.getString(hehe3),
                        cursor.getString(hehe4),
                        cursor.getString(hehe5),
                        cursor.getDouble(hehe6),
                        cursor.getDouble(hehe7),
                    )
                    happyPlaceList.add(place)
                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e:SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return happyPlaceList

    }
}

