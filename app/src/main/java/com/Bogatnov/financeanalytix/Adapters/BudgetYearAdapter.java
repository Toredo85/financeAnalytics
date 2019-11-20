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

public class BudgetYearAdapter extends RecyclerView.Adapter<BudgetYearAdapter.YearViewHolder> {

    private static final int CM_DELETE_ID = 1;
    private List<String> yearList = new ArrayList<>();
    private BudgetYearAdapter.OnYearClickListener onYearClickListener;
    private BudgetOperationListActivity activityIntent;
    private int position;
    private MyValueFormatter myValueFormatter;
    //private static final MyValueFormatter mvf = new MyValueFormatter("р.");

    public BudgetYearAdapter(BudgetYearAdapter.OnYearClickListener onYearClickListener, BudgetOperationListActivity activityIntent) {
        this.onYearClickListener = onYearClickListener;
        this.activityIntent = activityIntent;
    }

    @NonNull
    @Override
    public BudgetYearAdapter.YearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.year_row, parent, false);
        return new BudgetYearAdapter.YearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BudgetYearAdapter.YearViewHolder holder, int position) {
        holder.bind(yearList.get(position));
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
        return yearList.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class YearViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        private TextView yearTextView;

        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты
        public YearViewHolder(View itemView) {
            super(itemView);

            yearTextView = itemView.findViewById(R.id.date_year);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String year = yearList.get(getLayoutPosition());
                    onYearClickListener.onYearClick(year, activityIntent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(String year) {

            yearTextView.setText(year);

        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        }
    }
    public void setItems(Collection<String> years) {
        yearList.addAll(years);
        notifyDataSetChanged();
    }


    public void clearItems() {
        yearList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position, DBActions db){
        String year = yearList.get(position);
        db.delYear(year, "budgetoperations");
        notifyDataSetChanged();
    }

    public interface OnYearClickListener {
        void onYearClick(String year, BudgetOperationListActivity activityIntent);
    }
    public int getPosition() {
        return position;
    }

    public BudgetOperationListActivity getActivityIntent() {
        return activityIntent;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}
