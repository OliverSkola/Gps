package com.example.gps;

public class CaloriesBurned {

    private static double getMETValue(double avgVelocity, String activity){
        switch(activity){
            case "Gång": return Math.pow(1.01, 40 * (avgVelocity - 2.6)) + 1;
            case "Löpning": return Math.pow(1.1, 1.05 * avgVelocity) + 0.6 * avgVelocity + 0.05;
            case "Cykling": return Math.pow(1.5, 0.205 * avgVelocity) + 1.5;
            default: return 1.0;
        }
    }

    public static double getCalories(double weight, double duration, double avgVelocity, String activity){
        return (duration * (getMETValue(avgVelocity, activity) * 3.5 * weight)) / 200;
    }
}
