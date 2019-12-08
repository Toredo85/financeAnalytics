package com.Bogatnov.financeanalytix;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.Bogatnov.financeanalytix.Adapters.ColorGridAdapter;
import com.Bogatnov.financeanalytix.Entity.Category;

public class CategoryActivity extends AppCompatActivity implements OnClickListener{

    final String LOG_TAG = "myLogs";

    DBActions db;
    EditText categoryText;
    String color = "";
    GridView colorGrid;
    int categoryId;
    ArrayAdapter<String> gridColorAdapter;
    Button previousSelectedButton;
    Intent thisIntent;
    TextView errText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Button addButton = (Button) findViewById(R.id.add_button);
        categoryText = (EditText) findViewById(R.id.category);
        errText = (TextView) findViewById(R.id.errText);
        addButton.setOnClickListener(this);

        // создаем обработчик нажатия
        // создаем объект для создания и управления версиями БД
        // открываем подключение к БД
        db = new DBActions(this);
        db.open();


        // Строим сетку цветов
        String[] data = getResources().getStringArray(R.array.category_colors);
        gridColorAdapter = new ArrayAdapter<String>(this, R.layout.color_item, R.id.color_sqare, data);
        colorGrid = (GridView) findViewById(R.id.color_grid);
        colorGrid.setAdapter(new ColorGridAdapter(this, data));
        adjustGridView();
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                color = (String) parent.getItemAtPosition(position);
                Button buttonColorSelected = (Button) v;
                buttonColorSelected.setText("выбран");
                //buttonColorSelected.setsetForeground(getDrawable(R.drawable.home));
                if(previousSelectedButton != null){
                    previousSelectedButton.setText("");
                }
                previousSelectedButton = buttonColorSelected;
            }
        };
        colorGrid.setOnItemClickListener(itemListener);

        thisIntent = getIntent();
        if (thisIntent.hasExtra("_ID")) {
            Category category = db.getCategory(thisIntent.getIntExtra("_ID",0));
            categoryText.setText(category.getName());
            categoryId = category.getId();
            color = category.getColor();
            addButton.setText("готово");
            //previousSelectedButton = (Button) colorGrid.setSelection(2);
        }
        else {

        }
    }

    private void adjustGridView() {
        colorGrid.setNumColumns(3);
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
                if (thisIntent.hasExtra("_ID")) {
                    db.updateCategory(categoryId, name, color);
                    Log.d(LOG_TAG, "row updated, name = " + name);
                }
                else {
                    if (db.getCategoryByName(name) != null){
                        errText.setText("*Категория с таким именем уже существует");
                        errText.setVisibility(View.VISIBLE);
                        return;
                    }
                    db.addCategory(name, color);
                    Log.d(LOG_TAG, "row inserted, name = " + name);
                }

                break;
        }

        Intent intentAdd = new Intent(this, CategoryList.class);
        startActivity(intentAdd);
    }

    public String getSelectColor(){
        return color;
    }

}
