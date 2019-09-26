package com.Bogatnov.financeanalytix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Bogatnov.financeanalytix.Entity.Category;
import com.Bogatnov.financeanalytix.R;

import java.util.List;

public class CategoryAdapter  extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> categories) {

        super(context, R.layout.category, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.category, null);
        }
        ((TextView) convertView.findViewById(R.id.id))
                .setText(category.getId().toString());
        ((TextView) convertView.findViewById(R.id.name))
                .setText(category.getName());
        ((TextView) convertView.findViewById(R.id.color))
                .setText(category.getColor());
        return convertView;
    }
}
