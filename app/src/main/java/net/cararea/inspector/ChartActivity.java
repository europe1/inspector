package net.cararea.inspector;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ChartActivity extends AppCompatActivity implements ChartBuilder {
    private LineChart lineChart;
    private LineData data;
    private LineDataSet setComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        List<Entry> entries;
        switch (getIntent().getExtras().getString("chartType")) {
            case "speed":
                //entries = TripRecord.getInstance().getSpeedChart();
                entries = new ArrayList<>();
                entries.add(new Entry(1, 15));
                entries.add(new Entry(2, 15));
                entries.add(new Entry(3, 5));
                entries.add(new Entry(4, 10));
                break;
            case "rpm":
                entries = TripRecord.getInstance().getRpmChart();
                break;
            default:
                entries = new ArrayList<>();
        }
        createChart(entries);
        TripRecord.getInstance().setChart(this);
    }

    @Override
    public void updateChart(Entry entry) {
        setComp.addEntry(entry);
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void createChart(List<Entry> entries) {
        lineChart = findViewById(R.id.chart);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.enableGridDashedLine(20,10, 0);
        rightAxis.setDrawAxisLine(false);

        setComp = new LineDataSet(entries, "Company 1");
        setComp.setLineWidth(2);
        setComp.setDrawFilled(true);
        setComp.setColor(getResources().getColor(R.color.colorPrimary));
        setComp.setFillColor(getResources().getColor(R.color.colorLight));
        setComp.setCircleRadius(1);
        setComp.setCircleColor(getResources().getColor(R.color.colorPrimary));
        setComp.setDrawValues(false);

        data = new LineData(setComp);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TripRecord.getInstance().setChart(null);
    }
}
