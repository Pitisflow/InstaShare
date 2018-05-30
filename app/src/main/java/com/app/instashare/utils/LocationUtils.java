package com.app.instashare.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.instashare.R;
import com.app.instashare.ui.base.activity.MainActivity;
import com.app.instashare.ui.base.fragment.MainFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 22/5/18.
 */

public class LocationUtils {

    private static final double OFFSET = 0.0001;
    private static final double LONGITUDE_DISTANCE_IN_METERS = 8.417507;
    private static final double LATITUDE_DISTANCE_IN_METERS = 11.10533;
    private static final int KILOMETER = 1000;

    public static final int REQUEST_CHECK_SETTINGS_GPS = 3000;




    public static GeoPoint getMaxGeoPoint(int kilometers, Map<String, Object> currentLocation)
    {
        double currentLongitude = (double) currentLocation.get(Constants.USER_LONGITUDE_K);
        double currentLatitude = (double) currentLocation.get(Constants.USER_LATITUDE_K);

        double maxLongitude = currentLongitude + getMaxLongitudeCoordinate(kilometers);
        double maxLatitude = currentLatitude + getMaxLatitudeCoordinate(kilometers);

        GeoPoint maxPoint = new GeoPoint(maxLatitude, maxLongitude);

        return maxPoint;
    }


    public static GeoPoint getMinGeoPoint(int kilometers, Map<String, Object> currentLocation)
    {
        double currentLongitude = (double) currentLocation.get(Constants.USER_LONGITUDE_K);
        double currentLatitude = (double) currentLocation.get(Constants.USER_LATITUDE_K);

        double minLongitude = currentLongitude - getMaxLongitudeCoordinate(kilometers);
        double minLatitude = currentLatitude - getMaxLatitudeCoordinate(kilometers);

        GeoPoint minPoint = new GeoPoint(minLatitude, minLongitude);

        return minPoint;
    }


    private static double getMaxLongitudeCoordinate(int kilometers)
    {
        return (kilometers * KILOMETER * OFFSET) / LONGITUDE_DISTANCE_IN_METERS;
    }


    private static double getMaxLatitudeCoordinate(int kilometers)
    {
        return (kilometers * KILOMETER * OFFSET) / LATITUDE_DISTANCE_IN_METERS;
    }

    public static int getDistanceHypotenuse(int kilometers)
    {
        Double result = Math.sqrt(Math.pow(kilometers, 2) + Math.pow(kilometers, 2));
        return result.intValue();
    }

    public static String getDistanceBetween(Location location, Location location1, Context context)
    {
        int distance = Math.round(location.distanceTo(location1));

        if (distance < 1000) return context.getString(R.string.distance_less_a_km);
        else if (distance >= 1000 && distance < 2000) return context.getString(R.string.distance_a_km);
        else return context.getString(R.string.distance_more_a_km, distance / 1000);
    }

    public static Map<String, Double> getMapFromGeoPoint(GeoPoint point)
    {
        Map<String, Double> map = new HashMap<>();
        map.put(Constants.USER_LONGITUDE_K, point.getLongitude());
        map.put(Constants.USER_LATITUDE_K, point.getLatitude());

        return map;
    }

    public static GeoPoint getGeoPointFromMap(Map<String, Object> map)
    {
        double longitude = (double) map.get(Constants.USER_LONGITUDE_K);
        double latitude = (double) map.get(Constants.USER_LATITUDE_K);

        GeoPoint point = new GeoPoint(latitude, longitude);
        return point;
    }




    @SuppressLint("MissingPermission")
    public static void getMyLocation(GoogleApiClient apiClient, long interval,
                                     LocationListener listener, AppCompatActivity activity,
                                     LocationStatus locationStatus)
    {
        if(apiClient!=null && apiClient.isConnected())
        {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(interval);
            locationRequest.setFastestInterval(interval);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);


            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, listener);

            PendingResult<LocationSettingsResult> result = LocationServices
                    .SettingsApi.checkLocationSettings(apiClient, builder.build());

            result.setResultCallback(result1 -> {
                final Status status = result1.getStatus();

                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        locationStatus.stateSucces();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(activity,
                                    REQUEST_CHECK_SETTINGS_GPS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d("Exception", e.getMessage());
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        locationStatus.statusUnavailable();
                        break;
                }
            });
        }
    }


    public interface LocationStatus{
        void stateSucces();

        void statusUnavailable();
    }
}
