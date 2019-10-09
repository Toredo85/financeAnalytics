package com.Bogatnov.financeanalytix.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.Bogatnov.financeanalytix.CategoryActivity;

public class ColorGridAdapter extends BaseAdapter {
    private CategoryActivity context;
    private String[] products;

    public ColorGridAdapter(CategoryActivity context, String[] products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.length;
    }

    @Override
    public Object getItem(int position) {
        return products[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button colorButton;

        if (convertView == null) {
            colorButton = new Button(context);
            if (context.getSelectColor().matches(products[position])) {
                colorButton.setText("выбран");
            }
            colorButton.setBackgroundColor(Color.parseColor(products[position]));
            //colorButton.setWidth(5);
            //colorButton.setHeight(5);
            colorButton.setFocusable(false);
            colorButton.setClickable(false);
        } else {
            colorButton = (Button) convertView;
        }
        colorButton.setId(position);

        return colorButton;
    }



}
