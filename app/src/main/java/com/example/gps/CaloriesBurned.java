package com.example.gps;

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

    private double getMETValue(double avgVelocity, String activity){
        switch(activity){
            case "walking": return Math.pow(1.01, 40 * (avgVelocity - 2.6)) + 1;
            case "running": return Math.pow(1.1, 1.05 * avgVelocity) + 0.6 * avgVelocity + 0.05;
            case "cycling": return Math.pow(1.5, 0.205 * avgVelocity) + 1.5;
            default: return 1.0;
        }
    }

    private double getMETValue(){
        switch(activity){
            case "walking": return Math.pow(1.01, 40 * (avgVelocity - 2.6)) + 1;
            case "running": return Math.pow(1.1, 1.05 * avgVelocity) + 0.6 * avgVelocity + 0.05;
            case "cycling": return Math.pow(1.5, 0.205 * avgVelocity) + 1.5;
            default: return 1.0;
        }
    }

    public double getCalories(double weight, double duration, double avgVelocity, String activity){
            return (duration * (getMETValue(avgVelocity, activity) * 3.5 * weight)) / 200;
    }

    private double caloriesBurned() {
        return (duration * (getMETValue() * 3.5 * weight)) / 200;
    }
}
