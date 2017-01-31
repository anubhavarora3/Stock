package com.example.anubharora.stock.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anubharora.stock.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StockGraphActivity extends AppCompatActivity {

    private ArrayList<String> weekStockData = new ArrayList<String>();
    private ArrayList<String> weekDates = new ArrayList<>();
    private String name;
    private String symbol;

    private ArrayList<BarEntry> entries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<String>();

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_graph);

        barChart = (BarChart) findViewById(R.id.bar_chart);

        Bundle bundle;
        bundle = getIntent().getExtras();
        weekStockData = bundle.getStringArrayList("weekStockPrices");
        weekDates = bundle.getStringArrayList("weekDates");
        name = bundle.getString("name");
        symbol = bundle.getString("symbol");

        for(int i = 0; i < weekDates.size(); i++){
            entries.add(new BarEntry(i, Float.parseFloat(weekStockData.get(i))));
            labels.add(i,weekDates.get(i));
        }

        BarDataSet dataset = new BarDataSet(entries, "#StockPrices");

        BarData barData = new BarData(dataset);
        barChart.setData(barData);
        barChart.animateY(5000);

        XAxis xAxis;

        IAxisValueFormatter xAxisFormatter = new IndexAxisValueFormatter(labels);

        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);



    }
}
