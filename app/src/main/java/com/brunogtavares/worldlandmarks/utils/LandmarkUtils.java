package com.brunogtavares.worldlandmarks.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by brunogtavares on 10/16/18.
 */

public class LandmarkUtils {

    private LandmarkUtils(){}

    public static String getCountryName(Context context, List<FirebaseVisionLatLng> landmarkLocations) {

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
}
