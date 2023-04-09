package com.example.furniturefrenzy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class GameDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FurnitureFrenzy.db"

        const val TABLE_NAME = "game_records"
        const val COLUMN_ID = "id"
        const val COLUMN_DATETIME = "datetime"
        const val COLUMN_TIMETAKEN = "time_taken"
        const val COLUMN_SCORE = "score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_DATETIME INTEGER, " +
                "$COLUMN_TIMETAKEN INTEGER, " +
                "$COLUMN_SCORE INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addGameRecord(datetime: Long, timeTaken: Int, score: Int) {
        val uuid = UUID.randomUUID().toString()
        val values = ContentValues().apply {
            put(COLUMN_ID, uuid)
            put(COLUMN_DATETIME, datetime)
            put(COLUMN_TIMETAKEN, timeTaken)
            put(COLUMN_SCORE, score)
        }

        writableDatabase.insert(TABLE_NAME, null, values)
    }
}
