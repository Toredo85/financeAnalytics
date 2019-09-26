package com.Bogatnov.financeanalytix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDAO extends SQLiteOpenHelper {
    public AppDAO(Context context) {
        // конструктор суперкласса
        super(context, "fanalytixDB", null, 3);
    }
    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table categories ("
                + "_id integer primary key autoincrement,"
                + "name text,"
                + "color text" + ");");
        db.execSQL("create table cashmove ("
                + "_id integer primary key autoincrement,"
                + "date numeric,"
                + "direction text,"
                + "category integer,"
                + "amount numeric" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, " --- onUpgrade database from " + oldVersion
                + " to " + newVersion + " version --- ");

        if (oldVersion == 1 && newVersion == 2) {

            // данные для таблицы должностей

            db.beginTransaction();
            try {
                db.execSQL("create table cashmove ("
                        + "transactionid integer primary key autoincrement,"
                        + "date numeric,"
                        + "direction text,"
                        + "category integer,"
                        + "amount numeric" + ");");

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        if (oldVersion == 2 && newVersion == 3) {

            // данные для таблицы должностей

            db.beginTransaction();
            try {
                db.execSQL("DROP TABLE categories");
                db.execSQL("DROP TABLE cashmove");

                db.execSQL("create table categories ("
                        + "_id integer primary key autoincrement,"
                        + "name text,"
                        + "color text" + ");");
                db.execSQL("create table cashmove ("
                        + "_id integer primary key autoincrement,"
                        + "date numeric primary key,"
                        + "direction text primary key,"
                        + "category integer primary key,"
                        + "amount numeric" + ");");


                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }


}

