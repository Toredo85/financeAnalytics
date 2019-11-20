package com.Bogatnov.financeanalytix.Adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Bogatnov.financeanalytix.BudgetOperationListActivity;
import com.Bogatnov.financeanalytix.DBActions;
import com.Bogatnov.financeanalytix.Diagrams.MyValueFormatter;
import com.Bogatnov.financeanalytix.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BudgetMonthAdapter extends RecyclerView.Adapter<BudgetMonthAdapter.MonthViewHolder> {

    private static final int CM_DELETE_ID = 1;
    private List<String> monthList = new ArrayList<>();
    private BudgetMonthAdapter.OnMonthClickListener onMonthClickListener;
    private BudgetOperationListActivity activityIntent;
    private int position;
    private MyValueFormatter myValueFormatter= new MyValueFormatter(null);

    public BudgetMonthAdapter(BudgetMonthAdapter.OnMonthClickListener onMonthClickListener, BudgetOperationListActivity activityIntent) {
        this.onMonthClickListener = onMonthClickListener;
        this.activityIntent = activityIntent;
    }

    @NonNull
    @Override
    public BudgetMonthAdapter.MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.month_row, parent, false);
        return new BudgetMonthAdapter.MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BudgetMonthAdapter.MonthViewHolder holder, int position) {
        holder.bind(monthList.get(position));
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
        return monthList.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class MonthViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        private TextView monthTextView;

        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты
        public MonthViewHolder(View itemView) {
            super(itemView);

            monthTextView = itemView.findViewById(R.id.date_month);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String month = monthList.get(getLayoutPosition());
                    onMonthClickListener.onMonthClick(month, activityIntent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(String month) {

            monthTextView.setText(myValueFormatter.getMonthText(month));

        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        }
    }
    public void setItems(Collection<String> months) {
        monthList.addAll(months);
        notifyDataSetChanged();
    }


    public void clearItems() {
        monthList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position, DBActions db){
        String month = monthList.get(position);
        db.delMonth(month, "budgetoperations");
        notifyDataSetChanged();
    }

    public interface OnMonthClickListener {
        void onMonthClick(String month, BudgetOperationListActivity activityIntent);
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
