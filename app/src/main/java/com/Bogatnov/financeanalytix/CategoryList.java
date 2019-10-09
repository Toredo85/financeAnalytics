package com.Bogatnov.financeanalytix;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Bogatnov.financeanalytix.Adapters.CategoryAdapter;
import com.Bogatnov.financeanalytix.Entity.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;

public class CategoryList extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    DBActions db;
    Intent thisIntent;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    private void initRecyclerView() {


        categoriesRecyclerView = findViewById(R.id.category_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter.OnCategoryClickListener onCategoryClickListener = new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                thisIntent = getIntent();
                if (thisIntent.hasExtra("Select_option")) {
                  boolean selection = thisIntent.getBooleanExtra("Select_option", false);
                  if (selection) {
                      Intent intent = new Intent();
                      intent.putExtra("_id", category.getId());
                      intent.putExtra("name", category.getName());

                      setResult(RESULT_OK, intent);
                      finish();
                  }
                }
            }
        };
        categoryAdapter = new CategoryAdapter(onCategoryClickListener);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        registerForContextMenu(categoriesRecyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBActions(this);
        db.open();

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

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);

        initRecyclerView();
        loadCategories();
    }

    private void loadCategories() {
        Collection<Category> categories = getCategories();
        categoryAdapter.setItems(categories);
    }

    private Collection<Category> getCategories() {

        Cursor cursor = db.getAllDataCategories();
        ArrayList categoriesArray = new ArrayList<Category>();
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String color = cursor.getString(cursor.getColumnIndex("color"));

                categoriesArray.add(new Category(id, name, color));

            } while (cursor.moveToNext());
        }
        return categoriesArray;
    }

    public void onClick(View view) {

        // добавляем запись
        switch (view.getId()) {
            case R.id.fab:
                // добавляем запись
                Intent intentAdd = new Intent(this, CategoryActivity.class);
                startActivity(intentAdd);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка

            int position;
            try {
                position = categoryAdapter.getPosition();
            } catch (Exception e) {
                return super.onContextItemSelected(item);
            }


            // получаем новый курсор с данными
            categoryAdapter.deleteItem(position, db);
            categoryAdapter.clearItems();
            loadCategories();
            return true;

        }
        if (item.getItemId() == CM_EDIT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка

            int position;
            try {
                position = categoryAdapter.getPosition();
            } catch (Exception e) {
                return super.onContextItemSelected(item);
            }


            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra("_ID", categoryAdapter.getCategoryId(position));
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