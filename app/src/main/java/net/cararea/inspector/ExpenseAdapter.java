package net.cararea.inspector;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cararea.inspector.expenses.BaseExpense;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpensesViewHolder> {
    private List<BaseExpense> mDataSet;
    private Context context;

    static class ExpensesViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView image;

        ExpensesViewHolder(LinearLayout view) {
            super(view);
            name = view.findViewById(R.id.list_expense_name);
            price = view.findViewById(R.id.list_expense_price);
            image = view.findViewById(R.id.list_expense_image);
        }
    }

    ExpenseAdapter(Context context, List<BaseExpense> expenseList) {
        mDataSet = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_element, parent, false);
        return new ExpensesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesViewHolder holder, int position) {
        GradientDrawable typeShape = (GradientDrawable) holder.image.getDrawable();
        typeShape.setColor(ContextCompat.getColor(context, mDataSet.get(
                holder.getAdapterPosition()).getColor()));
        holder.name.setText(mDataSet.get(holder.getAdapterPosition()).getName());
        holder.price.setText(String.format(Locale.ENGLISH,
                "%.2fр.", mDataSet.get(holder.getAdapterPosition()).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
