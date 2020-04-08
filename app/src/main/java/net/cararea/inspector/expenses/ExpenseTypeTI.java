package net.cararea.inspector.expenses;

import net.cararea.inspector.R;

public final class ExpenseTypeTI extends ExpenseType {
    @Override
    public int getNameResourceId() { return R.string.expense_type_ti; }

    @Override
    public int getColor() { return R.color.colorTI; }

    @Override
    public BaseExpense newExpense() {
        return new TIExpense();
    }
}
