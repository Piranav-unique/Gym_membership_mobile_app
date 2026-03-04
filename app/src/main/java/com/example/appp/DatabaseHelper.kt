package com.example.appp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "GymDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_LOGS = "workout_logs"
        private const val COLUMN_ID = "id"
        private const val COLUMN_WORKOUT_NAME = "workout_name"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_LOGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WORKOUT_NAME + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOGS")
        onCreate(db)
    }

    fun logWorkout(workoutName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = sdf.format(Date())

        values.put(COLUMN_WORKOUT_NAME, workoutName)
        values.put(COLUMN_TIMESTAMP, currentDateAndTime)

        val id = db.insert(TABLE_LOGS, null, values)
        db.close()
        return id
    }

    fun getAllLogs(): List<String> {
        val logs = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LOGS ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_NAME))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                logs.add("$name at $time")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return logs
    }

    fun clearLogs() {
        val db = this.writableDatabase
        db.delete(TABLE_LOGS, null, null)
        db.close()
    }
}
