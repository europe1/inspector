package net.cararea.inspector;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        PriceViewModel viewModel;
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        TextView title = findViewById(R.id.report_title);
        TextView month = findViewById(R.id.last_month);
        TextView year = findViewById(R.id.last_year);
        TextView all = findViewById(R.id.all_time);
        TextView perMonth = findViewById(R.id.per_month);
        TextView perLitre = findViewById(R.id.per_litre);
        TextView perLitreText = findViewById(R.id.per_litre_text);

        title.setText(extras.getInt("title"));

        switch (extras.getInt("title")) {
            case R.string.expense_type_repair:
                viewModel = new RepairViewModel(this.getApplication());
                break;
            case R.string.expense_type_ti:
                viewModel = new TIViewModel(this.getApplication());
                break;
            case R.string.expense_type_other:
                viewModel = new OtherViewModel(this.getApplication());
                break;
            default:
                viewModel = new FuelViewModel(this.getApplication());
                perLitreText.setVisibility(View.VISIBLE);
                perLitre.setVisibility(View.VISIBLE);
                break;
        }

        viewModel.getMonthPrice().observe(this, (price) ->
                month.setText(String.format(Locale.ENGLISH, "%.2fр.", price)));
        viewModel.getYearPrice().observe(this, (price) ->
                year.setText(String.format(Locale.ENGLISH, "%.2fр.", price)));
        viewModel.getTotalPrice().observe(this, (price) -> {
            all.setText(String.format(Locale.ENGLISH, "%.2fр.", price));
            perMonth.setText(String.format(Locale.ENGLISH, "%.2fр.", price / 12));
        });
    }
}
