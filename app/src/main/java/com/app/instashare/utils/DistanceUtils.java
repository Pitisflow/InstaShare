package com.app.instashare.utils;

/**
 * Created by Pitisflow on 22/5/18.
 */

public class DistanceUtils {

    private static final double DISPLAY = 0.0001;
    private static final double LONGITUDE_DISTANCE_IN_METERS = 8.417507;
    private static final double LATITUDE_DISTANCE_IN_METERS = 11.10533;
    private static final int KILOMETER = 1000;


    public static double getMaxLongitudeCoordinate(int kilometers)
    {
        return (kilometers * KILOMETER * DISPLAY) / LONGITUDE_DISTANCE_IN_METERS;
    }


    public static double getMaxLatitudeCoordinate(int kilometers)
    {
        return (kilometers * KILOMETER * DISPLAY) / LATITUDE_DISTANCE_IN_METERS;
    }

    public static int getDistanceHypotenuse(int kilometers)
    {
        Double result = Math.sqrt(Math.pow(kilometers, 2) + Math.pow(kilometers, 2));
        return result.intValue();
    }
}
