package com.Bogatnov.financeanalytix;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class CategoryList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private static final int CM_DELETE_ID = 1;
    DBActions db;
    SimpleCursorAdapter scAdapter;
    TextView nameCategory;
    TextView idCategory;
    Intent thisIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisIntent = getIntent();
        if (thisIntent.hasExtra("Select_option")) {
            setContentView(R.layout.app_bar_category_list);
        }
        else {
            setContentView(R.layout.activity_category_list);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
        }

        final ListView categoryList;
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);
        // открываем подключение к БД
        db = new DBActions(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[]{"_id", "name", "color"};
        int[] to = new int[]{R.id.id, R.id.name, R.id.color};

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.category, null, from, to, 0);
        categoryList = (ListView) findViewById(R.id.category_list);
        nameCategory = (TextView) findViewById(R.id.name);
        idCategory = (TextView) findViewById(R.id.id);

        categoryList.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(categoryList);

        // создаем лоадер для чтения данных
        LoaderManager.getInstance(this).initLoader(0,null,this);
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                thisIntent = getIntent();
                if (thisIntent.hasExtra("Select_option")) {
                    boolean selection = thisIntent.getBooleanExtra("Select_option", false);
                    if (selection) {
                        Intent intent = new Intent();
                        Cursor client = (Cursor) parent.getItemAtPosition(position);
                        intent.putExtra("_id", client.getInt(0));
                        intent.putExtra("name", client.getString(1));

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    public void onClick(View view) {

        // добавляем запись
        switch (view.getId()) {
            case R.id.fab:
                // добавляем запись
                Intent intentAdd = new Intent(this, CategoryActivity.class);
                startActivity(intentAdd);
                // получаем новый курсор с данными
                LoaderManager.getInstance(this).getLoader(0).forceLoad();
        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();

            // подключаемся к БД
            db.delCategory(acmi.id);
            // получаем новый курсор с данными
            LoaderManager.getInstance(this).getLoader(0).forceLoad();
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
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AppCursorLoader(this, db, "Category list");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

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