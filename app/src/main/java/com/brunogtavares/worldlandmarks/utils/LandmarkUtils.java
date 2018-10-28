package com.brunogtavares.worldlandmarks.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brunogtavares on 10/16/18.
 */

public class LandmarkUtils {

    private LandmarkUtils(){}

    public static String getLocation(Context context, List<FirebaseVisionLatLng> landmarkLocations) {

        double latitude = 0d;
        double longitude = 0d;

        for (FirebaseVisionLatLng loc: landmarkLocations) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPercentage(double confidenceValue) {
        double percentage = confidenceValue * 100;
        DecimalFormat df2 = new DecimalFormat(".##");
        return df2.format(percentage) + "%";
    }

    public static double getLatitude(List<FirebaseVisionLatLng> landmarkLocations) {
        double latitude = 0.0;
        for (FirebaseVisionLatLng loc: landmarkLocations) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public static double getLongitude(List<FirebaseVisionLatLng> landmarkLocations) {
        double longitude = 0.0;
        for (FirebaseVisionLatLng loc: landmarkLocations) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }
}
