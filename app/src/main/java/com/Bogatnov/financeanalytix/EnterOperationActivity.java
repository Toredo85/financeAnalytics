package com.Bogatnov.financeanalytix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class EnterOperationActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    DBActions db;
    Integer categoryId;
    String categoryName;
    TextView categoryText;
    EditText amount;
    TextView date;
    String direction = "-";
    int DIALOG_DATE = 1;
    int myYear = 1;
    int myMonth = 1;
    int myDay = 1;
    String[] data = {"Приход", "Расход"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar cal = Calendar.getInstance();
        myYear = cal.get(Calendar.YEAR);
        myMonth = cal.get(Calendar.MONTH);
        myDay = cal.get(Calendar.DAY_OF_MONTH);
        setContentView(R.layout.activity_enter_operation);
        Button addButton = (Button) findViewById(R.id.add_button);
        categoryText = (TextView) findViewById(R.id.category);
        //categoryIdView = (TextView) findViewById(R.id.);
        amount = (EditText) findViewById(R.id.amount);
        date = (TextView) findViewById(R.id.date);
        // создаем объект для создания и управления версиями БД
        // открываем подключение к БД
        db = new DBActions(this);
        db.open();

        addButton.setOnClickListener(this);
        categoryText.setOnClickListener(this);

        // Адаптер спиннера
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spin_direction);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Направление");
        // выделяем элемент
        spinner.setSelection(1);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0){
                    direction = "+";
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#008577"));
                    ((TextView) parent.getChildAt(0)).setTextSize(24);
                }
                else{
                    direction = "-";
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                    ((TextView) parent.getChildAt(0)).setTextSize(24);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onClick(View view) {

        // получаем данные из полей ввода
        // подключаемся к БД
        switch (view.getId()){
            case R.id.add_button:

                Log.d(LOG_TAG, "--- Insert in table: cashmove---");
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                String amountValue = amount.getText().toString();
                String dateOperation = date.getText().toString();
                //categoryId = Integer.valueOf(categoryIdView.getText().toString());

                // вставляем запись и получаем ее ID
                db.addOperation(direction, categoryId, amountValue, dateOperation);
                Intent intent = new Intent(this, OperationListActivity.class);
                startActivity(intent);
                break;
                default:
                break;
            case R.id.category:
                Log.d(LOG_TAG, "click on category text");
                Intent intentSelect = new Intent(this, CategoryList.class);
                intentSelect.putExtra("Select_option", true);
                startActivityForResult(intentSelect, 1);
                break;
            case R.id.date:
                showDialog(DIALOG_DATE);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        categoryId = data.getIntExtra("_id",0);
        categoryName = data.getStringExtra("name");
        categoryText.setText(categoryName);
    }
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            date.setText("" + myYear + "-" + myMonth + "-" + myDay);
        }
    };
}
