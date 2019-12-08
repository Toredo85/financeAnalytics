package com.Bogatnov.financeanalytix;

import android.content.Intent;
import android.database.Cursor;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Bogatnov.financeanalytix.Adapters.CategoryAdapter;
import com.Bogatnov.financeanalytix.Adapters.OperationAdapter;
import com.Bogatnov.financeanalytix.Entity.Category;
import com.Bogatnov.financeanalytix.Entity.Operation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class OperationListActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    Intent thisIntent;
    String factTable = "cashmove";
    DBActions db;
    private RecyclerView operationsRecyclerView;
    private OperationAdapter operationAdapter;

    private void initRecyclerView() {
        operationsRecyclerView = findViewById(R.id.operation_recycler_view);
        operationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OperationAdapter.OnOperationClickListener onOperationClickListener = new OperationAdapter.OnOperationClickListener() {
            @Override
            public void onOperationClick(Operation operation) {

            }

            };
        operationAdapter = new OperationAdapter(onOperationClickListener);
        operationsRecyclerView.setAdapter(operationAdapter);
        registerForContextMenu(operationsRecyclerView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_list);

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

        initRecyclerView();

        Intent intent = getIntent();
        if (intent.hasExtra("filter")) {
            String filter = intent.getStringExtra("filter");
            String startDate = intent.getStringExtra("startdate");
            String endDate = intent.getStringExtra("enddate");
            loadOperationsByCategoryName(filter, startDate, endDate);
        }
        else {
            loadOperations();
        }
    }
    private void loadOperations() {
        Collection<Operation> operations = getOperations();
        operationAdapter.setItems(operations);
    }

    private void loadOperationsByCategoryName(String categoryName, String startDate, String endDate) {
        Collection<Operation> operations = getOperationsByCategoryName(categoryName, startDate, endDate);
        operationAdapter.setItems(operations);
    }

    private Collection<Operation> getOperations() {
        Cursor cursor = db.getAllDataOperations(factTable);
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

    private Collection<Operation> getOperationsByCategoryName(String categoryName, String startDate, String endDate) {
        Cursor cursor = db.getAllDataOperationsByCategoryName(factTable, categoryName, startDate, endDate);
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
                Intent intent = new Intent(this, EnterOperationActivity.class);
                intent.putExtra("table", factTable);
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
            loadOperations();
            return true;

        }
        if (item.getItemId() == CM_EDIT_ID) {
            int position;
            try {
                position = operationAdapter.getPosition();
            } catch (Exception e) {
                return super.onContextItemSelected(item);
            }

            Operation operation = operationAdapter.editItem(position, db);
            Intent intent = new Intent(this, EnterOperationActivity.class);
            intent.putExtra("_ID", operation.getId());
            intent.putExtra("table", "cashmove");
            startActivity(intent);
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
