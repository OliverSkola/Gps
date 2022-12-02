package com.example.gps;

import android.location.Location;

public class Angles {
    /**
     * @author Ludvig Andersson
     * Calculates the angle in degrees between two locations. Assumes altitude has already been set.
     * @param location1 The first point, seen as origin in calculations
     * @param location2 The second point, seen as the destination in calculations
     * @return The angle between the given locations in degrees, positive if the altitude of location2 > location1,
     * negative if altitude of location2 < location1
     */
    public static double getAngle(Location location1, Location location2){
        double distance = location1.distanceTo(location2);
        //if no distance between the points the angle between them is 0 (also avoids division by 0)
        if(distance == 0){
            return 0;
        }
        double altDiff = location2.getAltitude() - location1.getAltitude();
        return Math.toDegrees(Math.atan(altDiff/distance));
    }
}
