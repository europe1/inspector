package net.cararea.inspector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.cararea.inspector.db.Converters;
import net.cararea.inspector.db.Expense;
import net.cararea.inspector.expenses.BaseExpense;
import net.cararea.inspector.expenses.FuelExpense;
import net.cararea.inspector.expenses.OtherExpense;
import net.cararea.inspector.expenses.RepairExpense;
import net.cararea.inspector.expenses.TIExpense;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CreateExpenseFragment extends Fragment {
    private OnFragmentInteractionListener interactionListener;
    private long date;
    private BaseExpense expense;

    public CreateExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_expense, container,
                false);

        Spinner spinner = view.findViewById(R.id.expense_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.expense_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                       long id) {
                switch (position) {
                    case 1:
                        expense = new RepairExpense(getContext());
                        break;
                    case 2:
                        expense = new TIExpense(getContext());
                        break;
                    case 3:
                        expense = new OtherExpense(getContext());
                        break;
                    default:
                        expense = new FuelExpense(getContext());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expense = new FuelExpense(getContext());
            }
        });

        Button button = view.findViewById(R.id.expense_save);
        button.setOnClickListener(this::saveExpense);

        Button buttonDiscard = view.findViewById(R.id.expense_discard);
        buttonDiscard.setOnClickListener((v) ->
                ((CalendarActivity) getActivity()).hideAddExpense()
        );

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    void setDate(long date) {
        this.date = date;
    }

    private void saveExpense(View v) {
        TextView nameView = getView().findViewById(R.id.expense_name);
        TextView priceView = getView().findViewById(R.id.expense_price);

        String name = nameView.getText().toString();
        float price;

        try {
            price = Float.parseFloat(priceView.getText().toString());
        } catch (NumberFormatException e) {
            priceView.setError("This field can't be blank");
            priceView.requestFocus();
            return;
        }

        if (!name.trim().isEmpty())
            expense.setName(name);

        if (price >= 0)
            expense.setPrice(price);
        else
            expense.setPrice(0f);

        Calendar expenseDate = Converters.toCalendar(date);
        expense.setDate(expenseDate);

        onSave(expense.createDbObject());
    }

    private void onSave(Expense expense) {
        if (interactionListener != null) {
            interactionListener.onExpenseSave(expense);
        }
    }

    void resetForm() {
        EditText nameView = getView().findViewById(R.id.expense_name);
        EditText priceView = getView().findViewById(R.id.expense_price);

        View view = getView().findFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        nameView.getText().clear();
        priceView.getText().clear();
    }

    public interface OnFragmentInteractionListener {
        void onExpenseSave(Expense expense);
    }
}
