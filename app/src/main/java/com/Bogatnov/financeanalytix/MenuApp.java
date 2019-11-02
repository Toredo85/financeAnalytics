package com.Bogatnov.financeanalytix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;


public class MenuApp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DBActions db;
    TextView balance;
    TextView expreses;
    TextView planExpresses;
    private PieChart chart;
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
        balance.setText(String.format("%10.2f",db.getBalance("" + myYear + "-" + myMonth + "-" + myDay)));
        expreses.setText(String.format("%10.2f",db.getExpenses("" + myYear + "-" + myMonth + "-1",
                "" + myYear + "-" + myMonth + "-" + myDay, "cashmove")));
        planExpresses.setText(String.format("%10.2f",db.getExpenses("" + myYear + "-" + myMonth + "-1",
                "" + myYear + "-" + myMonth + "-" + myDay, "budgetoperations")));


        //Diagrams
        chart = findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText(generateCenterSpannableText());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);

        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);
        // add a selection listener
        //chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);

        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);

        onProgressChanged();
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

    public void onProgressChanged() {

        setData();

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Фактические расходы\nпо категориям");

        s.setSpan(new RelativeSizeSpan(1.7f), 0, 19, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 19, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 19, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.2f), 19, s.length(), 0);
        //s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);

        return s;

    }

    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());

    }

    public void onNothingSelected() {

        Log.i("PieChart", "nothing selected");

    }

    private void setData() {
        ArrayList<String> parties = new ArrayList<>();
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        Cursor cursor = db.getExpensesForDiagramPie("" + myYear + "-" + myMonth + "-1",
                "" + myYear + "-" + myMonth + "-" + myDay, "cashmove");
        float expencesAmount = db.getExpenses("" + myYear + "-" + myMonth + "-1",
                "" + myYear + "-" + myMonth + "-" + myDay, "cashmove").floatValue();
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColCategory = cursor.getColumnIndex("categoryname");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                parties.add(cursor.getString(idColCategory));

            } while (cursor.moveToNext());
        }
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColCategory = cursor.getColumnIndex("categoryname");
            int idColColor = cursor.getColumnIndex("color");
            int idColAmount = cursor.getColumnIndex("amount");
            int i = 0;
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                String category = cursor.getString(idColCategory);
                String colorCategory = cursor.getString(idColColor);
                float amount = cursor.getFloat(idColAmount);
                entries.add(new PieEntry((float) amount,
                        parties.get(i % parties.size()),
                        getResources().getDrawable(R.drawable.star)));
            } while (cursor.moveToNext());
        }
            PieDataSet dataSet = new PieDataSet(entries, "фактические расходы");
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());
            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chart));
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            //data.setValueTypeface(tfLight);
            chart.setData(data);

            // undo all highlights
            chart.highlightValues(null);
            chart.invalidate();
        }

}
