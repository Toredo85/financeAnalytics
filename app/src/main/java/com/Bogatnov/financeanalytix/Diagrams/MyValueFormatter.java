package com.Bogatnov.financeanalytix.Diagrams;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;

import com.github.mikephil.charting.formatter.ValueFormatter;



import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyValueFormatter extends ValueFormatter{

    private final DecimalFormat mFormat;
    private String suffix;

    public MyValueFormatter(String suffix) {

        mFormat = new DecimalFormat("###,###,###,##0.0");
        this.suffix = suffix;

    }

    @Override
    public String getFormattedValue(float value) {

        return mFormat.format(value) + suffix;

    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        if (axis instanceof XAxis) {
            return mFormat.format(value);
        } else if (value > 0) {
            return mFormat.format(value) + suffix;
        } else {
            return mFormat.format(value);
        }
    }

    public String getMonthText(String value) {

        if (value.equals("01")) {
            return "Январь";
        } else if (value.equals("02")) {
            return "Февраль";
        } else if (value.equals("03")) {
            return "Март";
        } else if (value.equals("04")) {
            return "Апрель";
        } else if (value.equals("05")) {
            return "Май";
        } else if (value.equals("06")) {
            return "Июнь";
        } else if (value.equals("07")) {
            return "Июль";
        } else if (value.equals("08")) {
            return "Август";
        } else if (value.equals("09")) {
            return "Сентябрь";
        } else if (value.equals("10")) {
            return "Октябрь";
        } else if (value.equals("11")) {
            return "Ноябрь";
        } else if (value.equals("12")) {
            return "Декабрь";
        }
        return "Месяц не определен";
    }
    public String getMonthText(int value) {

        if (value == 1) {
            return "Январь";
        } else if (value == 2) {
            return "Февраль";
        } else if (value == 3) {
            return "Март";
        } else if (value == 4) {
            return "Апрель";
        } else if (value == 5) {
            return "Май";
        } else if (value == 6) {
            return "Июнь";
        } else if (value == 7) {
            return "Июль";
        } else if (value == 8) {
            return "Август";
        } else if (value == 9) {
            return "Сентябрь";
        } else if (value == 10) {
            return "Октябрь";
        } else if (value == 11) {
            return "Ноябрь";
        } else if (value == 12) {
            return "Декабрь";
        }
        return "Месяц не определен";
    }
}