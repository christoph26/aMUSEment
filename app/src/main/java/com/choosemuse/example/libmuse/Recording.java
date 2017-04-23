package com.choosemuse.example.libmuse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoph on 23.04.17.
 */

public class Recording {
    private double sumVals = 0.0;
    private int countVals = 0;

    private List<Double> samples = new ArrayList<Double>();

    public void addSample(double value) {
        sumVals += value;
        countVals++;
        samples.add(value);
    }

    public double getAverage() {
        return sumVals/countVals;
    }
}

