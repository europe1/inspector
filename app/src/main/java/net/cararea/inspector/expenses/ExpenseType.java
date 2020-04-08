package net.cararea.inspector.expenses;

public abstract class ExpenseType {
    abstract public int getNameResourceId();
    abstract public int getColor();
    abstract public BaseExpense newExpense();
}
