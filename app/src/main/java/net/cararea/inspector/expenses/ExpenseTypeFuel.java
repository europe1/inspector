package net.cararea.inspector.expenses;

import net.cararea.inspector.R;

public final class ExpenseTypeFuel extends ExpenseType {
    @Override
    public int getNameResourceId() { return R.string.expense_type_fuel; }

    @Override
    public int getColor() { return R.color.colorFuel; }

    @Override
    public BaseExpense newExpense() {
        return new FuelExpense();
    }
}
