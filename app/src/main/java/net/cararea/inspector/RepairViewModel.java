package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.expenses.ExpenseTypeRepair;

class RepairViewModel extends PriceViewModel {
    RepairViewModel(Application application) {
        super(application, new ExpenseTypeRepair());
    }
}
