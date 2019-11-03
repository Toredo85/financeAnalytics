package com.Bogatnov.financeanalytix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.Bogatnov.financeanalytix.Diagrams.MyValueFormatter;
import com.Bogatnov.financeanalytix.Diagrams.PieDiagram;
import com.Bogatnov.financeanalytix.Diagrams.StackedBarDiagram;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;


public class MenuApp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DBActions db;
    TextView balance;
    TextView expreses;
    TextView planExpresses;
    private PieChart chart;
    private CombinedChart chart2;
    int myYear;
    int myMonth;
    int myDay;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getThis(), EnterOperationActivity.class);
                intent.putExtra("table", "cashmove");
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // создаем объект для создания и управления версиями БД
        // открываем подключение к БД
        db = new DBActions(this);
        db.open();
        balance = (TextView) findViewById(R.id.textBalance);
        expreses = (TextView) findViewById(R.id.textFactExpreses);
        planExpresses = (TextView) findViewById(R.id.textPlan);
        Calendar cal = Calendar.getInstance();
        myYear = cal.get(Calendar.YEAR);
        myMonth = cal.get(Calendar.MONTH)+1;
        myDay = cal.get(Calendar.DAY_OF_MONTH);
        String startMonth = "" + myYear;
        String startDate = "" + myYear;
        String currentDate = "" + myYear;
        if (myMonth < 10){
            startMonth = startMonth + "-0" + myMonth + "-01";
            currentDate = currentDate + "-0" + myMonth;}
            else{
            startMonth = startMonth + "-" + myMonth + "-01";
            currentDate = currentDate + "-" + myMonth;
        }
//        if (myMonth-2 < 10){
//            startDate = startDate + "-0" + (myMonth-2) + "-01";}
//            else{
//            startDate = startDate + "-" + (myMonth-2) + "-01";
//        }
        if (myDay < 10){
        currentDate = currentDate + "-0" + myDay;}
            else{
        currentDate = currentDate + "-" + myDay;}

       // balance.setText(String.format("%10.2f",db.getBalance(currentDate)));
        balance.setText(new MyValueFormatter("р.").getFormattedValue(db.getBalance(currentDate).floatValue()));
        expreses.setText(new MyValueFormatter("р.").getFormattedValue(db.getExpenses(startMonth, currentDate, "cashmove").floatValue()));
        planExpresses.setText(new MyValueFormatter("р.").getFormattedValue(db.getExpenses(startMonth, currentDate, "budgetoperations").floatValue()));

        //Diagrams
        chart = findViewById(R.id.chart1);
        PieDiagram pieExpences = new PieDiagram(chart);
        pieExpences.createDiagram();
        pieExpences.onShowExpences(db, "cashmove", startMonth, currentDate);

        chart2 = findViewById(R.id.chart2);

        Cursor cursor = db.getExpensesForMonth(startMonth, currentDate, "budgetoperations", "cashmove");
        StackedBarDiagram diagramPlanFact = new StackedBarDiagram(chart2, cursor);
        diagramPlanFact.createDiagram();
        int month_3 = myMonth-2;
        String myMont_3 = String.valueOf(month_3);
//        diagramPlanFact.onShowDiagram(cursor);

    }

    public void setContentView(){
        setContentView(R.layout.activity_main);
    }

    public MenuApp getThis(){
        return this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
