package com.Bogatnov.financeanalytix.DataPickerModules;

import android.graphics.Paint;
import android.graphics.Rect;

public class dpValuesSize {
    public int dpWidth = 0; //Ширина нашего текста
    public int dpHeight = 0; //Высота нашего текста
    public String dpValue = ""; //Значения текста
    public int dpTextSize = 0; // Размер шрифта
    public int valpadding = 30; //Отступ между значениями
    public int valinnerLeftpadding = 20; //Отступ по краям у значения

    /*
Нам необходимо подогнать размер шрифта таким образом, чтобы значение максимально плотно влезало в доверенное ему поле. Грубо говоря - текст должен быть такого размера, чтобы полностью вмещался в наш View, но при этом не вылазил бы за его границы.

Решение на мой взгляд не совсем элегантное. В цикле мы увеличиваем размер шрифта до тех пор, пока он не будет больше нашего поля. Как только размер превышен - останавливаем цикл и берем предыдущее значение.
Более элегантного алгоритма я не придумал, поэтому буду рад любым идеям и комментариям к данному алгоритму
*/
    public dpValuesSize(String val, int canvasW, int canvasH) {
        try {
            int maxTextHeight = (canvasH - (valpadding * 2)) / 2;
            boolean sizeOK = false;
            dpValue = val;
            while (!sizeOK) {
                Rect textBounds = new Rect();
                Paint textPaint = new Paint();
                dpTextSize++;
                textPaint.setTextSize(dpTextSize);

                textPaint.getTextBounds(val, 0, val.length(), textBounds);
                if (textBounds.width() <= canvasW - (valinnerLeftpadding * 2) && textBounds.height() <= maxTextHeight) {
                    dpWidth = textBounds.width();
                    dpHeight = textBounds.height();
                } else {
                    sizeOK = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
