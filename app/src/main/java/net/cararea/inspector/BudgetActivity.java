package net.cararea.inspector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class BudgetActivity extends AppCompatActivity {
    private FuelViewModel fuelViewModel;
    private RepairViewModel repairViewModel;
    private TIViewModel tiViewModel;
    private OtherViewModel otherViewModel;

    private TextView fuelText;
    private TextView repairText;
    private TextView tiText;
    private TextView otherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        fuelText = findViewById(R.id.textTopLeft);
        repairText = findViewById(R.id.textTopRight);
        tiText = findViewById(R.id.textBottomLeft);
        otherText = findViewById(R.id.textBottomRight);

        fuelViewModel = ViewModelProviders.of(this).get(FuelViewModel.class);
        repairViewModel = ViewModelProviders.of(this).get(RepairViewModel.class);
        tiViewModel = ViewModelProviders.of(this).get(TIViewModel.class);
        otherViewModel = ViewModelProviders.of(this).get(OtherViewModel.class);

        fuelViewModel.getMonthPrice().observe(this,
                (price) -> fuelText.setText(String.format("%.2fр.", price)));

        repairViewModel.getMonthPrice().observe(this,
                (price) -> repairText.setText(String.format("%.2fр.", price)));

        tiViewModel.getMonthPrice().observe(this,
                (price) -> tiText.setText(String.format("%.2fр.", price)));

        otherViewModel.getMonthPrice().observe(this,
                (price) -> otherText.setText(String.format("%.2fр.", price)));
    }

    public void monthClick(View v) {
        fuelViewModel.getMonthPrice().observe(this,
                (price) -> fuelText.setText(String.format("%.2fр.", price)));

        repairViewModel.getMonthPrice().observe(this,
                (price) -> repairText.setText(String.format("%.2fр.", price)));

        tiViewModel.getMonthPrice().observe(this,
                (price) -> tiText.setText(String.format("%.2fр.", price)));

        otherViewModel.getMonthPrice().observe(this,
                (price) -> otherText.setText(String.format("%.2fр.", price)));
    }

    public void yearClick(View v) {
        fuelViewModel.getYearPrice().observe(this,
                (price) -> fuelText.setText(String.format("%.2fр.", price)));

        repairViewModel.getYearPrice().observe(this,
                (price) -> repairText.setText(String.format("%.2fр.", price)));

        tiViewModel.getYearPrice().observe(this,
                (price) -> tiText.setText(String.format("%.2fр.", price)));

        otherViewModel.getYearPrice().observe(this,
                (price) -> otherText.setText(String.format("%.2fр.", price)));
    }

    public void allClick(View v) {
        fuelViewModel.getTotalPrice().observe(this,
                (price) -> fuelText.setText(String.format("%.2fр.", price)));

        repairViewModel.getTotalPrice().observe(this,
                (price) -> repairText.setText(String.format("%.2fр.", price)));

        tiViewModel.getTotalPrice().observe(this,
                (price) -> tiText.setText(String.format("%.2fр.", price)));

        otherViewModel.getTotalPrice().observe(this,
                (price) -> otherText.setText(String.format("%.2fр.", price)));
    }

    public void fuelClick(View v) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("title", R.string.expense_type_fuel);
        startActivity(intent);
    }

    public void repairClick(View v) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("title", R.string.expense_type_repair);
        startActivity(intent);
    }

    public void tiClick(View v) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("title", R.string.expense_type_ti);
        startActivity(intent);
    }

    public void otherClick(View v) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("title", R.string.expense_type_other);
        startActivity(intent);
    }
}
