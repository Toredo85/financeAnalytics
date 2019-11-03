package com.Bogatnov.financeanalytix.Diagrams;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

import com.Bogatnov.financeanalytix.DBActions;
import com.Bogatnov.financeanalytix.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class StackedBarDiagram  implements OnChartValueSelectedListener {

    private CombinedChart chart;
    private Cursor cursor;

    public StackedBarDiagram(CombinedChart  chart, Cursor cursor){ this.chart = chart; this.cursor = cursor; }

    public void createDiagram() {
        chart.setOnChartValueSelectedListener(this);
        chart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(40);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new MyValueFormatter("Р"));
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

        XAxis xLabels = chart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        xLabels.setGranularity(1f);
        xLabels.setAxisMinimum(.5f);
        xLabels.setValueFormatter(new CategoryValueFormatter(cursor));
        // chart.setDrawXLabels(false);
        // chart.setDrawYLabels(false);

        // setting data
//        seekBarX.setProgress(12);
//        seekBarY.setProgress(100);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        // chart.setDrawLegend(false);

        CombinedData data = new CombinedData();

        data.setData(generateLineData(cursor));
        data.setData(generateBarData(cursor));
        xLabels.setAxisMaximum(data.getXMax() + 0.5f);
        chart.setData(data);
        chart.invalidate();
    }
    private LineData generateLineData(Cursor cursor) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        int i = 0;
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColCategory = cursor.getColumnIndex("category");
            int idColPlanAmount = cursor.getColumnIndex("planamount");
            //int idColFactAmount = cursor.getColumnIndex("factamount");
            String category = "";
            do {
                i = i + 1;
                // получаем значения по номерам столбцов и пишем все в лог
                category = cursor.getString(idColCategory);
                entries.add(new Entry(i, cursor.getFloat(idColPlanAmount), new String[]{"planamount", category}));
                //values.add(new BarEntry(i, new float[]{val2}, category));

            } while (cursor.moveToNext());
        }
        LineDataSet set = new LineDataSet(entries, "План");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }
    private BarData generateBarData(Cursor cursor) {
        ArrayList<BarEntry> values = new ArrayList<>();
        int i = 0;
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColCategory = cursor.getColumnIndex("category");
            int idColPlanAmount = cursor.getColumnIndex("planamount");
            int idColFactAmount = cursor.getColumnIndex("factamount");
            String category = "";
            do {
                i = i + 1;
                // получаем значения по номерам столбцов и пишем все в лог
                //float val1 = (float) cursor.getFloat(idColPlanAmount);
                float val2 = (float) cursor.getFloat(idColFactAmount);
                category = cursor.getString(idColCategory);

                values.add(new BarEntry(i, new float[]{val2}, new String[]{"factamount", category}));

            } while (cursor.moveToNext());
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Факт");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Расходы (План)", "Расходы (Факт)"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new StackedValueFormatter(false, "", 1));
            data.setValueTextColor(Color.WHITE);

            return data;
        }
        return new BarData();
    }

    @Override

    public void onValueSelected(Entry e, Highlight h) {

            Log.i("VAL SELECTED", "Value: " + e.getData().toString());

    }

    @Override
    public void onNothingSelected() {}

    private int[] getColors() {

        // have as many colors as stack-values per entry

        int[] colors = new int[1];
        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 1);
        return colors;

    }
}
