package com.brunogtavares.worldlandmarks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brunogtavares on 10/20/18.
 */

public class MyLandmark implements Parcelable {

    private String landMarkName;
    private String confidence;
    private String place;
    private String imageUri;
    private double latitude;
    private double longitude;
    private String description;

    public MyLandmark(){}

    public MyLandmark(String landMarkName, String confidence, String place){
        this.landMarkName = landMarkName;
        this.confidence = confidence;
        this.place = place;
    }

    public MyLandmark(String landMarkName, String confidence, String place, double latitude,
                      double longitude) {
        this.landMarkName = landMarkName;
        this.confidence = confidence;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MyLandmark(String landMarkName, String confidence, String place, String imageUri,
                      double latitude, double longitude) {
        this.landMarkName = landMarkName;
        this.confidence = confidence;
        this.place = place;
        this.imageUri = imageUri;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MyLandmark(String landMarkName, String confidence, String place, String imageUri,
                      double latitude, double longitude, String description) {
        this.landMarkName = landMarkName;
        this.confidence = confidence;
        this.place = place;
        this.imageUri = imageUri;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public String getLandmark() {
        return landMarkName;
    }

    public void setLandMarkName(String landMarkName) {
        this.landMarkName = landMarkName;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getLocation() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.landMarkName);
        dest.writeString(this.confidence);
        dest.writeString(this.place);
        dest.writeString(this.imageUri);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.description);
    }

    protected MyLandmark(Parcel in) {
        this.landMarkName = in.readString();
        this.confidence = in.readString();
        this.place = in.readString();
        this.imageUri = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<MyLandmark> CREATOR = new Parcelable.Creator<MyLandmark>() {

        @Override
        public MyLandmark createFromParcel(Parcel source) {
            return new MyLandmark(source);
        }

        @Override
        public MyLandmark[] newArray(int size) {
            return new MyLandmark[size];
        }
    };
}
