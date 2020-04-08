package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.expenses.ExpenseTypeTI;

class TIViewModel extends PriceViewModel {
    TIViewModel(Application application) {
        super(application, new ExpenseTypeTI());
    }
}
