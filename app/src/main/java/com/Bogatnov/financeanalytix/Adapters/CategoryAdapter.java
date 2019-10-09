package com.Bogatnov.financeanalytix.Adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.Bogatnov.financeanalytix.DBActions;
import com.Bogatnov.financeanalytix.Entity.Category;
import com.Bogatnov.financeanalytix.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private List<Category> categoryList = new ArrayList<>();
    private OnCategoryClickListener onCategoryClickListener;
    private int position;

    public CategoryAdapter(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        private TextView nameTextView;
        private TextView idTextView;
        private TextView colorTextView;


        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты
        public CategoryViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name);
            idTextView = itemView.findViewById(R.id._id);
            colorTextView = itemView.findViewById(R.id.color);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category category = categoryList.get(getLayoutPosition());
                    onCategoryClickListener.onCategoryClick(category);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Category category) {
            nameTextView.setText(category.getName());
//            idTextView.setText(String.valueOf(category.getId()));
            colorTextView.setText(category.getColor());

            if (!category.getColor().isEmpty()){
                colorTextView.setTextColor(Color.parseColor(category.getColor()));
                colorTextView.setBackgroundColor(Color.parseColor(category.getColor()));
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
            menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        }
    }
    public void setItems(Collection<Category> categories) {
        categoryList.addAll(categories);
        notifyDataSetChanged();
    }


    public void clearItems() {
        categoryList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position, DBActions db){
        Category category = categoryList.get(position);
        db.delCategory(category.getId());
        notifyDataSetChanged();
    }

    public int getCategoryId(int position){
        Category category = categoryList.get(position);
        return category.getId();
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
