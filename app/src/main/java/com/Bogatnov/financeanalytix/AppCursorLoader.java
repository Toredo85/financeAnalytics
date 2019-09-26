package com.Bogatnov.financeanalytix;

import android.content.Context;
import android.database.Cursor;

import androidx.loader.content.CursorLoader;

public class AppCursorLoader extends CursorLoader {

    DBActions db;
    String activity;
    Context context;

    public AppCursorLoader(Context context, DBActions db, String activity) {
        super(context);
        this.db = db;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public Cursor loadInBackground() {

        if (activity == "Category list") {
            Cursor cursor = db.getAllDataCategories();
            return cursor;
        }
        if (activity == "Operation list") {
            Cursor cursor = db.getAllDataOperations();
            return cursor;
        }
        else {
        return db.getEmptyCursor();}
    }

}

