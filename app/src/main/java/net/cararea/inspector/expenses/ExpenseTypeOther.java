package net.cararea.inspector.expenses;

import net.cararea.inspector.R;

public final class ExpenseTypeOther extends ExpenseType {
    @Override
    public int getNameResourceId() { return R.string.expense_type_other; }

    @Override
    public int getColor() { return R.color.colorOther; }

    @Override
    public BaseExpense newExpense() {
        return new OtherExpense();
    }
}
