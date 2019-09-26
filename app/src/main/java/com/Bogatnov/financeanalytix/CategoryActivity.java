package com.Bogatnov.financeanalytix;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CategoryActivity extends AppCompatActivity implements OnClickListener {

    final String LOG_TAG = "myLogs";

    DBActions db;
    EditText categoryText;
    String color = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Button addButton = (Button) findViewById(R.id.add_button);
        categoryText = (EditText) findViewById(R.id.category);
        addButton.setOnClickListener(this);
        // создаем обработчик нажатия
        // создаем объект для создания и управления версиями БД
        // открываем подключение к БД
        db = new DBActions(this);
        db.open();

    }

    @Override
    public void onClick(View view) {

        // получаем данные из полей ввода
        String name = categoryText.getText().toString();

        // подключаемся к БД

        switch (view.getId()) {
            case R.id.add_button:
                Log.d(LOG_TAG, "--- Insert in table: categories---");
                // подготовим данные для вставки в виде пар: наименование столбца - значение

                // вставляем запись и получаем ее ID
                db.addCategory(name, color);
                Log.d(LOG_TAG, "row inserted, name = " + name);
                break;
        }

        Intent intentAdd = new Intent(this, CategoryList.class);
        startActivity(intentAdd);
    }
}
