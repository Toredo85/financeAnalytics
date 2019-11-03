package com.Bogatnov.financeanalytix;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.Bogatnov.financeanalytix.Entity.Category;

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
    public Cursor getAllDataOperations(String table) {
        String sqlQuery = "select "
                + "CM._id as _id,"
                + "CM.date as date, "
                + "CM.category as categoryid, "
                + "CM.direction as direction,"
                + "CM.amount as amount, "
                + "C.name as categoryname, "
                + "C.color as color "
                + "from " + table + " as CM "
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

    public void addOperation(String direction, int categoryId, String amount, String date, String table) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DIRECTION, direction);
        cv.put(COLUMN_CATEGORY_ID, categoryId);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_DATE, date);

        mDB.insert(table, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delCategory(long id) {
        mDB.delete(DB_TABLE_CATEGORIES, COLUMN_ID + " = " + id, null);
    }

    // получить запись из DB_TABLE
    public Category getCategory(long id) {
        Category category = new Category(0,"","");
        String selection = "_id = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };
        Cursor c = mDB.query(DB_TABLE_CATEGORIES, null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()){
            int categoryId = (c.getInt(c.getColumnIndex("_id")));
            String categoryName = (c.getString(c.getColumnIndex("name")));
            String categoryColor = (c.getString(c.getColumnIndex("color")));

            return new Category(categoryId,categoryName,categoryColor);
        }
        return new Category(0,"","");
    }

    // изменить запись из DB_TABLE
    public void updateCategory(long id, String name, String color) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COLOR, color);
        mDB.update(DB_TABLE_CATEGORIES, cv,COLUMN_ID + " = " + id, null);
    }
    // удалить операцию
    public void delOperation(long id, String table) {
        mDB.delete(table, COLUMN_ID + " = " + id, null);
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
    public Double getExpenses(String startMonthDate, String currentDate, String table) {

        Cursor cursor = mDB.rawQuery("Select sum(amount) as expenses from " + table + " where direction = ? and date between ? and ?", new String[] {"-", startMonthDate, currentDate});

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

    public Cursor getExpensesForDiagramPie(String startMonthDate, String currentDate, String table) {


        Cursor cursor = mDB.rawQuery("select "
                + "Sum(CM.amount) as amount, "
                + "C.name as categoryname, "
                + "C.color as color "
                + "from " + table + " as CM "
                + "inner join categories as C "
                + "on CM.category = C._id "
                + "where direction = ? and date between ? and ? "
                + "group by C.name, C.color", new String[] {"-", startMonthDate, currentDate});


        return cursor;
    }
    public Cursor getExpensesForMonth(String startDate, String endDate, String tableBudget, String tableFact) {

        Cursor cursor = mDB.rawQuery(
                "select "
                + "Sum(t.planamount) as planamount, "
                + "Sum(t.factamount) as factamount, "
                + "t.category as category from("
                    + "select "
                    + "TP.amount as planamount, "
                    + "0 as factamount, "
                    + "C.name as category "
                    + "from " + tableBudget + " as TP "
                    + "inner join categories as C "
                    + "on TP.category = C._id "
                    + "where direction = ? and date between ? and ? "
                    + "union all "
                    + "select "
                    + "0, "
                    + "TF.amount, "
                    + "C.name "
                    + "from " + tableFact + " as TF "
                    + "inner join categories as C "
                    + "on TF.category = C._id "
                    + "where direction = ? and date between ? and ? )t group by category", new String[] {"-", startDate, endDate, "-",startDate, endDate});

        return cursor;
    }
}
