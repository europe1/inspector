package net.cararea.inspector.expenses;

import net.cararea.inspector.db.DatabaseObject;
import net.cararea.inspector.db.Expense;

import java.util.Calendar;

import static net.cararea.inspector.GarageActivity.getSelectedCarId;

public abstract class BaseExpense implements DatabaseObject<Expense> {
    abstract public String getName();
    abstract public void setName(String newName);
    abstract public float getPrice();
    abstract public void setPrice(float newPrice);
    abstract public Calendar getDate();
    abstract public void setDate(Calendar newDate);
    abstract protected ExpenseType getType();

    public int getColor() {
        return getType().getColor();
    }

    public Expense createDbObject() {
        Expense expense = new Expense();
        expense.name = getName();
        expense.price = getPrice();
        expense.type = getType();
        expense.date = getDate();
        expense.carId = getSelectedCarId();
        return expense;
    }
}
