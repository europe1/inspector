package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.expenses.ExpenseTypeFuel;

class FuelViewModel extends PriceViewModel {
    FuelViewModel(Application application) {
        super(application, new ExpenseTypeFuel());
    }
}
