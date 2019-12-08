package com.Bogatnov.financeanalytix.Diagrams;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.Bogatnov.financeanalytix.DBActions;
import com.Bogatnov.financeanalytix.OperationListActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class PieDiagram implements OnChartValueSelectedListener {
    private PieChart chart;
    Context activity;

    public PieDiagram(PieChart chart){ this.chart = chart;}

    public void createDiagram(Context activity) {

        this.activity = activity;

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
        chart.setOnChartValueSelectedListener(this);
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
    }

    public void onShowExpences(DBActions db, String table, String startDate, String endDate) {

        ArrayList<String> parties = new ArrayList<>();
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        Cursor cursor = db.getExpensesForDiagramPie(startDate, endDate, table);

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
                        category));
            } while (cursor.moveToNext());
        }
        setData(parties, entries);

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
        PieEntry pieEntry = (PieEntry) e;
        if (pieEntry == null)
            return;

        Intent intent = new Intent(activity, OperationListActivity.class);
        intent.putExtra("filter", String.valueOf(pieEntry.getLabel()));
        activity.startActivity(intent);


        Log.i("VAL SELECTED",
                "Value: " + pieEntry.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());

    }

    public void onNothingSelected() {

        Log.i("PieChart", "nothing selected");

    }

    private void setData(ArrayList<String> parties, ArrayList<PieEntry> entries) {

        PieDataSet dataSet = new PieDataSet(entries, null);
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
