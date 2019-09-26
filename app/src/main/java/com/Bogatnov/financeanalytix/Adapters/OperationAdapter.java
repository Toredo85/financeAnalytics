package com.Bogatnov.financeanalytix.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.Bogatnov.financeanalytix.Entity.Category;
import com.Bogatnov.financeanalytix.Entity.Operation;
import com.Bogatnov.financeanalytix.R;

import java.util.List;
import java.util.Map;

public class OperationAdapter  extends SimpleAdapter {
    public OperationAdapter(Context context,
                            List<? extends Map<String, ?>> data, int resource,
                            String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
    @Override
    public void setViewText(TextView v, String text) {
        // метод супер-класса, который вставляет текст
        super.setViewText(v, text);
        // если нужный нам TextView, то разрисовываем
        if (v.getId() == R.id.amount) {
            int i = Integer.parseInt(text);
            if (i < 0) v.setTextColor(Color.RED); else
            if (i > 0) v.setTextColor(Color.GREEN);
        }
    }

    @Override
    public void setViewImage(ImageView v, int value) {
        // метод супер-класса
        super.setViewImage(v, value);
        // разрисовываем ImageView
//        if (value == negative) v.setImageResource(R.drawable.minusflat_105990); else
//        if (value == positive) v.setImageResource(R.drawable.minusflat_105990);
//    }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Operation operation = getItem(position);
//
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext())
//                        .inflate(R.layout.operatoin_row, null);
//            }
//            ((TextView) convertView.findViewById(R.id.id))
//                    .setText(operation.getTransactionid().toString());
//            ((TextView) convertView.findViewById(R.id.category))
//                    .setText(operation.getCategory().getName());
//            ((TextView) convertView.findViewById(R.id.amount))
//                    .setText(String.valueOf(operation.getAmount()));
//            return convertView;
        }
}
