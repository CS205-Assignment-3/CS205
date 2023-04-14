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

    fun getAllRecords(): List<Record> {
        val records = mutableListOf<Record>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val uuidIndex = cursor.getColumnIndex(COLUMN_ID)
                val dateTimeIndex = cursor.getColumnIndex(COLUMN_DATETIME)
                val timeTakenIndex = cursor.getColumnIndex(COLUMN_TIMETAKEN)
                val scoreIndex = cursor.getColumnIndex(COLUMN_SCORE)

                val uuid = if (uuidIndex != -1) cursor.getString(uuidIndex) else ""
                val dateTime = if (dateTimeIndex != -1) cursor.getString(dateTimeIndex) else ""
                val timeTaken = if (timeTakenIndex != -1) cursor.getInt(timeTakenIndex) else 0
                val score = if (scoreIndex != -1) cursor.getInt(scoreIndex) else 0

                val record = Record(uuid, dateTime, timeTaken, score)
                records.add(record)
            } while (cursor.moveToNext())
        }


        cursor.close()
        return records
    }

    fun deleteAllRecords() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }
}
