package net.cararea.inspector.expenses;

import net.cararea.inspector.R;

public final class ExpenseTypeRepair extends ExpenseType {
    @Override
    public int getNameResourceId() { return R.string.expense_type_repair; }

    @Override
    public int getColor() { return R.color.colorRepair; }

    @Override
    public BaseExpense newExpense() {
        return new RepairExpense();
    }
}
