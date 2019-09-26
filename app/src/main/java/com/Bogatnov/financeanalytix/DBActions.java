package com.Bogatnov.financeanalytix;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DBActions {
    private static final String DB_NAME = "fanalytixDB";
    private static final String DB_TABLE_CATEGORIES = "categories";
    private static final String DB_TABLE_OPERATIONS = "cashmove";

    private final Context mCtx;
    private AppDAO mDBHelper;
    private SQLiteDatabase mDB;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";

    public static final String COLUMN_DIRECTION = "direction";
    public static final String COLUMN_CATEGORY_ID = "category";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";

    public DBActions(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new AppDAO(mCtx);
        mDB = mDBHelper.getWritableDatabase();
    }
    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllDataCategories() {
        return mDB.query(DB_TABLE_CATEGORIES, null, null, null, null, null, null);
    }

    // получить пустой cursor
    public Cursor getEmptyCursor() {
        return mDB.rawQuery("Select null from category where false", null);
    }
    public Cursor getAllDataOperations() {
        String sqlQuery = "select "
                + "CM._id as _id,"
                + "CM.date as date, "
                + "CM.category as categoryid, "
                + "CM.direction as direction,"
                + "CM.amount as amount, "
                + "C.name as categoryname, "
                + "C.color as color "
                + "from cashmove as CM "
                + "inner join categories as C "
                + "on CM.category = C._id "
                + "order by date";
        return mDB.rawQuery(sqlQuery, null);
    }

    // добавить запись в DB_TABLE
    public void addCategory(String name, String color) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COLOR, color);
        mDB.insert(DB_TABLE_CATEGORIES, null, cv);
    }

    public void addOperation(String direction, int categoryId, String amount, String date) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DIRECTION, direction);
        cv.put(COLUMN_CATEGORY_ID, categoryId);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_DATE, date);

        mDB.insert(DB_TABLE_OPERATIONS, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delCategory(long id) {
        mDB.delete(DB_TABLE_CATEGORIES, COLUMN_ID + " = " + id, null);
    }

    // удалить операцию
    public void delOperation(long id) {
        mDB.delete(DB_TABLE_OPERATIONS, COLUMN_ID + " = " + id, null);
    }

    public Double getBalance(String currentDate) {

        Cursor cursor = mDB.rawQuery("Select sum(case when direction = '+' then amount else -amount end) as balance from cashmove where date <= '" +  currentDate + "'", null);

        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColBalance = cursor.getColumnIndex("balance");
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                return cursor.getDouble(idColBalance);

            } while (cursor.moveToNext());

        }
        return Double.valueOf(0);
    }
    public Double getExpenses(String startMonthDate, String currentDate) {

        Cursor cursor = mDB.rawQuery("Select sum(amount) as expenses from cashmove where direction = ? and date between ? and ?", new String[] {"-", startMonthDate, currentDate});

        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColExpenses = cursor.getColumnIndex("expenses");
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                return cursor.getDouble(idColExpenses);

            } while (cursor.moveToNext());

        }
        return Double.valueOf(0);
    }
}
