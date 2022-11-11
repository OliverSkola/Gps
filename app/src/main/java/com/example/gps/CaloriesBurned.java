package com.example.gps;

import java.util.HashMap;

public class CaloriesBurned {
    double weight;
    double duration;
    double avgVelocity;
    HashMap<Double, Double> METValues;

    public CaloriesBurned(double weight, double duration, double avgVelocity, String activity) {
        this.weight = weight;
        this.duration = duration;
        this.avgVelocity = avgVelocity;
        METValues = new HashMap<>();
        met(activity);
    }

    private void met(String activity) {
        switch (activity) {
            case "cycling":
                METValues.put(17.5, 6.8);
                METValues.put(22.0, 8.0);
                METValues.put(27.5, 12.0);
                METValues.put(32.0, 15.0);
                break;
            case "running":
                METValues.put(6.4, 6.0);
                METValues.put(8.0, 8.3);
                METValues.put(9.6, 9.8);
                METValues.put(11.2, 11.0);
                METValues.put(12.9, 11.8);
                METValues.put(14.8, 12.8);
                METValues.put(16.1, 14.5);
                METValues.put(17.7, 16.0);
                METValues.put(19.3, 19.0);
                METValues.put(20.9, 19.8);
                METValues.put(22.5, 23.5);
                break;
            case "walking":
                METValues.put(4.0, 3.0);
                METValues.put(4.5, 3.5);
                METValues.put(5.6, 4.3);
                METValues.put(6.4, 5.0);
                METValues.put(7.2, 7.0);
                METValues.put(8.0, 9.8);
                break;
            default: break;
        }

    }

    private double caloriesBurned() {
        return duration * (METValues.get(avgVelocity) * 3.5 * weight) / 200;
    }

}
