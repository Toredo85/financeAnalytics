package com.Bogatnov.financeanalytix;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Bogatnov.financeanalytix.Adapters.BudgetMonthAdapter;
import com.Bogatnov.financeanalytix.Adapters.BudgetOperationAdapter;
import com.Bogatnov.financeanalytix.Adapters.BudgetYearAdapter;
import com.Bogatnov.financeanalytix.Entity.Category;
import com.Bogatnov.financeanalytix.Entity.Operation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;

public class BudgetOperationListActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;
    Intent thisIntent;
    String planTable = "budgetoperations";
    DBActions db;
    private RecyclerView operationsRecyclerView;
    private BudgetOperationAdapter operationAdapter;
    private BudgetYearAdapter yearAdapter;
    private BudgetMonthAdapter monthAdapter;

    private void initRecyclerView() {
        operationsRecyclerView = findViewById(R.id.operation_recycler_view);
        operationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BudgetOperationAdapter.OnOperationClickListener onOperationClickListener = new BudgetOperationAdapter.OnOperationClickListener() {
            @Override
            public void onOperationClick(Operation operation) {
            }
        };
        operationAdapter = new BudgetOperationAdapter(onOperationClickListener);
        operationsRecyclerView.setAdapter(operationAdapter);
        registerForContextMenu(operationsRecyclerView);
    }

    private void initRecyclerMonthView() {
        operationsRecyclerView = findViewById(R.id.operation_recycler_view);
        operationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BudgetMonthAdapter.OnMonthClickListener onMonthClickListener = new BudgetMonthAdapter.OnMonthClickListener() {
            @Override
            public void onMonthClick(String month, BudgetOperationListActivity activityIntent) {
                Intent intent = new Intent(activityIntent, BudgetOperationListActivity.class);
                intent.putExtra("month", month);
                startActivity(intent);
            }

        };
        monthAdapter = new BudgetMonthAdapter(onMonthClickListener, this);
        operationsRecyclerView.setAdapter(monthAdapter);
        registerForContextMenu(operationsRecyclerView);
    }

    private void initRecyclerYearView() {
        operationsRecyclerView = findViewById(R.id.operation_recycler_view);
        operationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BudgetYearAdapter.OnYearClickListener onYearClickListener = new BudgetYearAdapter.OnYearClickListener() {
            @Override
            public void onYearClick(String year, BudgetOperationListActivity activityIntent) {
                Intent intent = new Intent(activityIntent, BudgetOperationListActivity.class);
                intent.putExtra("year", year);
                startActivity(intent);
            }

        };
        yearAdapter = new BudgetYearAdapter(onYearClickListener, this);
        operationsRecyclerView.setAdapter(yearAdapter);
        registerForContextMenu(operationsRecyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_list);

        thisIntent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ListView operationsListView;
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);

        // открываем подключение к БД
        db = new DBActions(this);
        db.open();
        if (thisIntent.hasExtra("year")){
            initRecyclerMonthView();
            loadMonthOperations(thisIntent.getStringExtra("year"));
        }
        else if (thisIntent.hasExtra("month")) {
            initRecyclerView();
            loadOperations(thisIntent.getStringExtra("month"));
        }
        else {
            initRecyclerYearView();
            loadYearOperations();
        }
    }

    private void loadOperations(String month) {
        Collection<Operation> operations = getOperations(month);
        operationAdapter.setItems(operations);
    }

    private void loadMonthOperations(String year) {
        Collection<String> months = getMonths(year);
        monthAdapter.setItems(months);
    }

    private void loadYearOperations() {
        Collection<String> years = getYears();
        yearAdapter.setItems(years);
    }

    private Collection<String> getMonths(String year) {

        Cursor cursor = db.getAllMonthsOperations(planTable, year);
        ArrayList monthsArray = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            do {
                String idDate = cursor.getString(cursor.getColumnIndex("date"));
                monthsArray.add(idDate);

            } while (cursor.moveToNext());
        }
        return monthsArray;
    }

    private Collection<String> getYears() {

        Cursor cursor = db.getAllYearsOperations(planTable);
        ArrayList yearsArray = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            do {
                String idDate = cursor.getString(cursor.getColumnIndex("date"));
                yearsArray.add(idDate);

            } while (cursor.moveToNext());
        }
        return yearsArray;
    }


    private Collection<Operation> getOperations(String month) {

        Cursor cursor = db.getAllOperationsForMonth(planTable, month);
        ArrayList operationsArray = new ArrayList<Operation>();
        if(cursor.moveToFirst()) {
            do {
                int idCategory = cursor.getInt(cursor.getColumnIndex("categoryid"));
                String name = cursor.getString(cursor.getColumnIndex("categoryname"));
                String color = cursor.getString(cursor.getColumnIndex("color"));

                Category category = new Category(idCategory, name, color);

                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String direction = cursor.getString(cursor.getColumnIndex("direction"));
                Double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

                operationsArray.add(new Operation(id, date, direction, category, amount));

            } while (cursor.moveToNext());
        }
        return operationsArray;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab:
                // добавляем запись
                Intent intent = new Intent(this, EnterBudgetActivity.class);
                startActivity(intent);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            // получаем новый курсор с данными

            int position;
            try {
                position = operationAdapter.getPosition();
            } catch (Exception e) {
                return super.onContextItemSelected(item);
            }

            operationAdapter.deleteItem(position, db);
            operationAdapter.clearItems();
            if (thisIntent.hasExtra("year")){
                initRecyclerMonthView();
                loadMonthOperations(thisIntent.getStringExtra("year"));
            }
            if (thisIntent.hasExtra("month")) {
                loadOperations(thisIntent.getStringExtra("month"));

            }
            else {
                initRecyclerYearView();
                loadYearOperations();
            }

            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @NonNull

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bubget_operation_list) {
            Intent intent = new Intent(this, BudgetOperationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_plan_operation_list) {
            Intent intent = new Intent(this, PlanOperationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_operation_list) {
            Intent intent = new Intent(this, OperationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_category_list) {
            Intent intent = new Intent(this, CategoryList.class);
            startActivity(intent);
        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_add) {
            Intent intent = new Intent(this, EnterOperationActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
