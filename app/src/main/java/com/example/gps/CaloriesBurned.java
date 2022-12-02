package com.example.gps;

import java.util.HashMap;

public class CaloriesBurned {
    double weight;
    double duration;
    double avgVelocity;
    String activity;

    public CaloriesBurned(double weight, double duration, double avgVelocity, String activity) {
        this.weight = weight;
        this.duration = duration;
        this.avgVelocity = avgVelocity;
        this.activity = activity;
    }

    private double metEquation(){
        switch(activity){
            case "walking": return Math.pow(1.01, 40 * (avgVelocity - 2.6)) + 1;
            case "running": return Math.pow(1.1, 1.05 * avgVelocity) + 0.6 * avgVelocity + 0.05;
            case "cycling": return Math.pow(1.5, 0.205 * avgVelocity) + 1.5;
            default: return 1.0;
        }
    }

    private double caloriesBurned() {
        return (duration * (metEquation() * 3.5 * weight)) / 200;
    }
}
