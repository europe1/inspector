package net.cararea.inspector.expenses;

import android.content.Context;

import java.util.Calendar;

public class FuelExpense extends BaseExpense {
    private String name;
    public static ExpenseType type = new ExpenseTypeFuel();
    private float price;
    private Calendar date;

    FuelExpense() { name = "none"; price = 0; }

    public FuelExpense(Context context) { name = context.getString(type.getNameResourceId()); }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public void setPrice(float newPrice) {
        price = newPrice;
    }

    @Override
    public Calendar getDate() {
        return date;
    }

    @Override
    public void setDate(Calendar newDate) {
        date = newDate;
    }

    @Override
    protected ExpenseType getType() {
        return type;
    }
}
