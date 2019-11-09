package com.Bogatnov.financeanalytix.Adapters;

import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Bogatnov.financeanalytix.DBActions;
import com.Bogatnov.financeanalytix.Diagrams.MyValueFormatter;
import com.Bogatnov.financeanalytix.Entity.Operation;
import com.Bogatnov.financeanalytix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class PlanOperationAdapter extends RecyclerView.Adapter<PlanOperationAdapter.OperationViewHolder> {

    private static final int CM_DELETE_ID = 1;
    private List<Operation> operationList = new ArrayList<>();
    private PlanOperationAdapter.OnOperationClickListener onOperationClickListener;
    private int position;
    private static final MyValueFormatter mvf = new MyValueFormatter("р.");

    public PlanOperationAdapter(PlanOperationAdapter.OnOperationClickListener onOperationClickListener) {
        this.onOperationClickListener = onOperationClickListener;
    }

    @NonNull
    @Override
    public PlanOperationAdapter.OperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.operation_row, parent, false);
        return new PlanOperationAdapter.OperationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlanOperationAdapter.OperationViewHolder holder, int position) {
        holder.bind(operationList.get(position));
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
        return operationList.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class OperationViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        private TextView categoryTextView;
        private TextView idTextView;
        private TextView dateTextView;
        private TextView directionTextView;
        private TextView amountTextView;
        private LinearLayout colorOperation;

        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты
        public OperationViewHolder(View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.category);
            colorOperation = itemView.findViewById(R.id.color_operation);
            dateTextView = itemView.findViewById(R.id.date);
            //directionTextView = itemView.findViewById(R.id.direction);
            amountTextView = itemView.findViewById(R.id.amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Operation operation = operationList.get(getLayoutPosition());
                    onOperationClickListener.onOperationClick(operation);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Operation operation) {
            categoryTextView.setText(operation.getCategory().getName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            Date sDate = null;
            String dateOperation = null;
            try {
                sDate = sdf.parse(operation.getDate());
                SimpleDateFormat sdfOut = new SimpleDateFormat("dd.MM.yyyy");
                dateOperation = sdfOut.format(sDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTextView.setText(dateOperation);
            String direct = String.valueOf(operation.getDirection());
            directionTextView.setText(direct);
            if (direct.equals("+")) {
                amountTextView.setText(mvf.getFormattedValue((float) operation.getAmount()));
            } else {
                amountTextView.setText("-" + mvf.getFormattedValue((float) operation.getAmount()));
            }

            if (direct.equals("-")) {
                directionTextView.setTextColor(Color.RED);
                amountTextView.setTextColor(Color.RED);
                }
            if (direct.equals("+")) {
                directionTextView.setTextColor(Color.GREEN);
                amountTextView.setTextColor(Color.GREEN);
                }
            if (!operation.getCategory().getColor().isEmpty()){
                colorOperation.setBackgroundColor(Color.parseColor(operation.getCategory().getColor()));
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        }
    }
    public void setItems(Collection<Operation> operations) {
        operationList.addAll(operations);
        notifyDataSetChanged();
    }


    public void clearItems() {
        operationList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position, DBActions db){
        Operation operation = operationList.get(position);
        db.delOperation(operation.getId(), "plancashmove");
        notifyDataSetChanged();
    }

    public interface OnOperationClickListener {
        void onOperationClick(Operation operation);
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
