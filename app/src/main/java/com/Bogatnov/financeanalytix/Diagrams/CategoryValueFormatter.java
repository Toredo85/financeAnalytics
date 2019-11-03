package com.Bogatnov.financeanalytix.Diagrams;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CategoryValueFormatter extends ValueFormatter {

    private Cursor cursor;
    private HashMap<Integer, String> categories;

    public CategoryValueFormatter(Cursor cursor){
        this.cursor = cursor;
        this.categories = new HashMap<>();
        int i = 0;
        if (cursor.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColCategory = cursor.getColumnIndex("category");
            String category = "";
            do {
                i = i + 1;
                // получаем значения по номерам столбцов и пишем все в лог
                category = cursor.getString(idColCategory);
                this.categories.put(i, category);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public String getFormattedValue(float value) {

        String category = "";

        category = this.categories.get((int)value);

        return category;
    }
}
