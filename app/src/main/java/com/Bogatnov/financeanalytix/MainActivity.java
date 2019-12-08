package com.Bogatnov.financeanalytix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.Bogatnov.financeanalytix.Diagrams.MyValueFormatter;
import com.Bogatnov.financeanalytix.Diagrams.PieDiagram;
import com.Bogatnov.financeanalytix.Diagrams.StackedBarDiagram;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DBActions db;
    TextView balance;
    TextView expreses;
    TextView planExpresses;
    TextView vCurrentPeriod;
    private PieChart chart;
    private CombinedChart chart2;
    int myYear;
    int myMonth;
    int myDay;
    String currentDate;
    MyValueFormatter mvf;
    PieDiagram pieExpences;
    StackedBarDiagram diagramPlanFact;

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
        vCurrentPeriod = (TextView) findViewById(R.id.current_period);
        Button bLeftPeriod = (Button) findViewById(R.id.month_left);
        Button bRightPeriod = (Button) findViewById(R.id.month_right);
        bLeftPeriod.setOnClickListener(this);
        bRightPeriod.setOnClickListener(this);
        Calendar cal = Calendar.getInstance();
        myYear = cal.get(Calendar.YEAR);
        myMonth = cal.get(Calendar.MONTH)+1;
        myDay = cal.get(Calendar.DAY_OF_MONTH);
        String startMonth = "" + myYear;
        String startDate = "" + myYear;
        currentDate = "" + myYear;
        mvf = new MyValueFormatter("р.");
        if (myMonth < 10){
            startMonth = startMonth + "-0" + myMonth + "-01";
            currentDate = currentDate + "-0" + myMonth;}
        else{
            startMonth = startMonth + "-" + myMonth + "-01";
            currentDate = currentDate + "-" + myMonth;
        }

        if (myDay < 10){
            currentDate = currentDate + "-0" + myDay;}
        else{
            currentDate = currentDate + "-" + myDay;}

        setCurrentPeriod();


        balance.setText(mvf.getFormattedValue(db.getBalance(currentDate).floatValue()));
        expreses.setText(mvf.getFormattedValue(db.getExpenses(startMonth, currentDate, "cashmove").floatValue()));
        planExpresses.setText(mvf.getFormattedValue(db.getExpenses(startMonth, currentDate, "budgetoperations").floatValue()));

        //Diagrams
        chart = findViewById(R.id.chart1);
        pieExpences = new PieDiagram(chart);
        pieExpences.createDiagram(this);
        pieExpences.onShowExpences(db, "cashmove", startMonth, currentDate);

        chart2 = findViewById(R.id.chart2);

        Cursor cursor = db.getExpensesForMonth(startMonth, currentDate, "budgetoperations", "cashmove");
        diagramPlanFact = new StackedBarDiagram();
        diagramPlanFact.createDiagram(chart2, cursor);
        int month_3 = myMonth-2;
        String myMont_3 = String.valueOf(month_3);

    }

    private void setCurrentPeriod() {
        vCurrentPeriod.setText(mvf.getMonthText(myMonth) + " " + myYear);
    }

    public void setContentView(){
        setContentView(R.layout.activity_main);
    }

    public MainActivity getThis(){
        return this;
    }

    @Override
    public void onClick(View view) {

        // получаем данные из полей ввода

        // подключаемся к БД

        switch (view.getId()) {
            case R.id.month_left:
                if (myMonth == 1){
                    myMonth = 12;
                    myYear = myYear - 1;
                }
                else{
                    myMonth = myMonth - 1;
                }
                break;
            case R.id.month_right:
                if (myMonth == 12){
                    myMonth = 1;
                    myYear = myYear + 1;
                }
                else{
                    myMonth = myMonth + 1;
                }
                break;
        }
        String startMonth = "" + myYear;
        String startDate = "" + myYear;
        currentDate = "" + myYear;
        mvf = new MyValueFormatter("р.");
        if (myMonth < 10){
            startMonth = startMonth + "-0" + myMonth + "-01";
            currentDate = currentDate + "-0" + myMonth;}
        else{
            startMonth = startMonth + "-" + myMonth + "-01";
            currentDate = currentDate + "-" + myMonth;
        }
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH)+1;

        if (myMonth == currentMonth){
            if (myDay < 10){
                currentDate = currentDate + "-0" + myDay;
            }
            else{
                currentDate = currentDate + "-" + myDay;
            }
        }
        else{
            currentDate = currentDate + "-31";
        }
        setCurrentPeriod();

        expreses.setText(mvf.getFormattedValue(db.getExpenses(startMonth, currentDate, "cashmove").floatValue()));
        planExpresses.setText(mvf.getFormattedValue(db.getExpenses(startMonth, currentDate, "budgetoperations").floatValue()));

        pieExpences.onShowExpences(db, "cashmove", startMonth, currentDate);
        Cursor cursor = db.getExpensesForMonth(startMonth, currentDate, "budgetoperations", "cashmove");
        diagramPlanFact.createDiagram(chart2, cursor);

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
