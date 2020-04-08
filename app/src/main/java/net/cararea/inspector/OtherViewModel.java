package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.expenses.ExpenseTypeOther;

class OtherViewModel extends PriceViewModel {
    OtherViewModel(Application application) {
        super(application, new ExpenseTypeOther());
    }
}
