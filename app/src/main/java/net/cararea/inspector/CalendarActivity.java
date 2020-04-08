package net.cararea.inspector;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.domain.Event;

import net.cararea.inspector.db.Expense;
import net.cararea.inspector.expenses.BaseExpense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class CalendarActivity extends AppCompatActivity implements
        CreateExpenseFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener {
    private final static SimpleDateFormat keyFormat = new SimpleDateFormat("dd.MM.yy",
            Locale.ENGLISH);

    private Date selectedDate;
    private Calendar currentDate;

    private CreateExpenseFragment expenseFragment;

    private List<BaseExpense> expenseList = new ArrayList<>();
    private HashMap<String, List<BaseExpense>> dailyExpenses = new HashMap<>();

    private MonthViewModel viewModel;
    private RecyclerView dayView;
    private ExpenseAdapter adapter;

    private CalendarFragment calendar;
    private TextView dateText;
    private Button btnAddExpense;
    private View expenseContainer;

    private Animation slideUp;
    private Animation slideDown;

    private SimpleDateFormat dayFormat = new SimpleDateFormat(
            "dd MMM yyyy", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat(
            "MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendar = new CalendarFragment();
        expenseFragment = new CreateExpenseFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.calendar_fragment, calendar);
        transaction.add(R.id.new_expense_container, expenseFragment);
        transaction.commit();

        expenseContainer = findViewById(R.id.new_expense_container);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);

        btnAddExpense = findViewById(R.id.buttonAdd);
        dayView = findViewById(R.id.dayView);
        dateText = findViewById(R.id.calendar_date);

        currentDate = Calendar.getInstance();
        Calendar calendarFrom = (Calendar) currentDate.clone();
        Calendar calendarTo = (Calendar) currentDate.clone();
        selectedDate = currentDate.getTime();

        calendarFrom.add(Calendar.YEAR, -1);
        calendarTo.add(Calendar.YEAR, 1);

        RecyclerView.LayoutManager dayLayoutManager = new LinearLayoutManager(this);
        dayView.setLayoutManager(dayLayoutManager);

        RecyclerView.ItemAnimator animator = new SlideInLeftAnimator();
        animator.setAddDuration(300);
        dayView.setItemAnimator(animator);

        adapter = new ExpenseAdapter(this, expenseList);
        dayView.setAdapter(adapter);

        viewModel = new MonthViewModel(getApplication(), calendarFrom, calendarTo,
                GarageActivity.getSelectedCarId());
        LiveData<List<Expense>> yearExpenses = viewModel.getAll();
        yearExpenses.observe(this, (days) -> {
            List<Event> events = new ArrayList<>();
            dailyExpenses.clear();
            for (Expense expense : days) {
                Calendar expenseDate = expense.date;

                BaseExpense expenseToAdd = expense.type.newExpense();
                expenseToAdd.setName(expense.name);
                expenseToAdd.setPrice(expense.price);
                expenseToAdd.setDate(expenseDate);

                Date date = expenseDate.getTime();
                String day = keyFormat.format(date);

                if (!dailyExpenses.containsKey(day)) {
                    dailyExpenses.put(day, new ArrayList<>());
                }
                dailyExpenses.get(day).add(expenseToAdd);

                setToMidnight(expenseDate);
                events.add(new Event(ContextCompat.getColor(this, expenseToAdd.getColor()),
                        expenseToAdd.getDate().getTimeInMillis()));
            }
            calendar.addEvents(events);
            addDayEvents(selectedDate);
        });


        if (savedInstanceState != null && savedInstanceState.getLong("day") != 0) {
            if (savedInstanceState.getBoolean("fragmentVisible")) {
                showAddExpense(null);
            } else {
                hideAddExpense();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (expenseFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(expenseFragment).commit();
            expenseFragment = null;
        }

        if (calendar != null) {
            getSupportFragmentManager().beginTransaction().remove(calendar).commit();
            calendar = null;
        }

        super.onSaveInstanceState(outState);
        outState.putLong("day", selectedDate.getTime());
        outState.putBoolean("fragmentVisible", isFragmentVisible());
    }

    @Override
    public void onBackPressed() {
        if (isFragmentVisible()) {
            hideAddExpense();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onExpenseSave(Expense expense) {
        viewModel.insert(expense);
        expenseFragment.resetForm();
        hideAddExpense();
    }

    @Override
    public void onSelectDay(Date day) {
        selectedDate = day;

        dateText.setText(dayFormat.format(day));

        if (isFragmentVisible())
            hideAddExpense();

        if (day.getTime() > currentDate.getTimeInMillis())
            hideUI();
        else
            showUI();

        addDayEvents(day);
    }

    @Override
    public void onSelectMonth(Date month) {
        dateText.setText(monthFormat.format(month));
        calendar.restoreSelectionColor(this);
        addDayEvents(month);
        adapter.notifyDataSetChanged();
    }

    public void showAddExpense(View v) {
        if (isFragmentVisible())
            return;

        hideUI();
        expenseFragment.setDate(selectedDate.getTime());
        expenseContainer.startAnimation(slideUp);
        expenseContainer.setVisibility(View.VISIBLE);
        View price = findViewById(R.id.expense_price);
        if (price != null) {
            price.requestFocus();
        }
    }

    public void hideAddExpense() {
        expenseContainer.startAnimation(slideDown);
        expenseContainer.setVisibility(View.GONE);
        showUI();
    }

    private void showUI() {
        btnAddExpense.setVisibility(View.VISIBLE);
        dayView.setVisibility(View.VISIBLE);
    }

    private void hideUI() {
        btnAddExpense.setVisibility(View.GONE);
        dayView.setVisibility(View.GONE);
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    // FIXME: improve so it can be animated
    private void addDayEvents(Date date) {
        expenseList.clear();
        adapter.notifyDataSetChanged();
        if (dailyExpenses.containsKey(keyFormat.format(date))) {
            for (BaseExpense dayExpense : dailyExpenses.get(keyFormat.format(date))) {
                expenseList.add(0, dayExpense);
                adapter.notifyItemInserted(0);
            }

            calendar.setSelectionColor(ContextCompat.getColor(this,
                    expenseList.get(0).getColor()));
        } else {
            calendar.restoreSelectionColor(this);
        }
    }

    private boolean isFragmentVisible() {
        return expenseContainer.getVisibility() == View.VISIBLE;
    }
}
