package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_result);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        chart.getAxisLeft().setTextColor(Color.WHITE); // left y-axis
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);

        List<Entry> entries = new ArrayList<Entry>();
        List<Double> raw = MuseConnection.getInstance().getRecording().getSamples();

        for(int i=0;  i < raw.size(); i++) {
            entries.add(new Entry((float)i, (float)raw.get(i).doubleValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Score of a muse"); // add entries to dataset
        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCircleRadius(1f);

        // styling, .
        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);
        chart.animateX(3000);
    }
}