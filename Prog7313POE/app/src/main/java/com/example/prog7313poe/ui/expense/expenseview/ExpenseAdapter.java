package com.example.prog7313poe.ui.expense.expenseview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prog7313poe.Database.Expenses.ExpenseData;
import com.example.prog7313poe.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<ExpenseData> expenses = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseName, tvExpenseCategory, tvExpenseAmount, tvExpenseDate;

        public ViewHolder(View view) {
            super(view);
            tvExpenseName = view.findViewById(R.id.tvExpenseName);
            tvExpenseCategory = view.findViewById(R.id.tvExpenseCategory);
            tvExpenseAmount = view.findViewById(R.id.tvExpenseAmount);
            tvExpenseDate = view.findViewById(R.id.tvExpenseDate);
        }
    }

    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseAdapter.ViewHolder holder, int position) {
        ExpenseData expense = expenses.get(position);
        holder.tvExpenseName.setText(expense.getExpenseName());
        holder.tvExpenseCategory.setText("Category: " + expense.getExpenseCategory());
        holder.tvExpenseAmount.setText("Amount: R" + expense.getExpenseAmount());
        holder.tvExpenseDate.setText("Date: " + expense.getExpenseStartTime());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<ExpenseData> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    public void filter(String query) {
        List<ExpenseData> filteredExpenses = new ArrayList<>();
        for (ExpenseData expense : expenses) {
            if (expense.getExpenseName().toLowerCase().contains(query.toLowerCase())) {
                filteredExpenses.add(expense);
            }
        }
        this.expenses = filteredExpenses;
        notifyDataSetChanged();
    }
}
