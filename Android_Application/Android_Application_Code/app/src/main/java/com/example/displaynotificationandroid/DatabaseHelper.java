package com.example.displaynotificationandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLClientInfoException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "notification_table";
    private static final String col1 = "ID";
    private static final String col2 = "name";
    private static final String col3 = "loss";
    private static final String col4 = "description";
    private static final String col5 = "image";
    private static final String col6 = "date";
    private static final String col7 = "time";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY " +
                             "KEY " + "AUTOINCREMENT, " + col2 + " TEXT, " + col3 + " TEXT, " + col4
                             + " TEXT, " + col5 + " TEXT, " + col6 + " TEXT, " + col7 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String name, String loss, String description, String image, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2, name);
        contentValues.put(col3, loss);
        contentValues.put(col4, description);
        contentValues.put(col5, image);
        contentValues.put(col6, date);
        contentValues.put(col7, time);

        Log.d(TAG, "addData: Adding: " + name);
        Log.d(TAG, "addData: Adding: " + loss);
        Log.d(TAG, "addData: Adding: " + description);
        Log.d(TAG, "addData: Adding: " + image);
        Log.d(TAG, "addData: Adding: " + date);
        Log.d(TAG, "addData: Adding: " + time);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
