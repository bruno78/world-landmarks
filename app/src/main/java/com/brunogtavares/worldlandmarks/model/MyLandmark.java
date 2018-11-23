package com.brunogtavares.worldlandmarks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brunogtavares on 10/20/18.
 */

public class MyLandmark implements Parcelable {

    private String id;
    private String landmarkName;
    private String confidence;
    private String location;
    private String imageUri;
    private String latitude;
    private String longitude;
    private String description;

    public MyLandmark(){}

    public MyLandmark(String landmarkName, String confidence, String location){
        this.landmarkName = landmarkName;
        this.confidence = confidence;
        this.location = location;
    }

    public MyLandmark(String landmarkName, String confidence, String location, String latitude,
                      String longitude) {
        this.landmarkName = landmarkName;
        this.confidence = confidence;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MyLandmark(String landmarkName, String confidence, String location, String imageUri,
                      String latitude, String longitude) {
        this.landmarkName = landmarkName;
        this.confidence = confidence;
        this.location = location;
        this.imageUri = imageUri;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MyLandmark(String landmarkName, String confidence, String location, String imageUri,
                      String latitude, String longitude, String description) {
        this.landmarkName = landmarkName;
        this.confidence = confidence;
        this.location = location;
        this.imageUri = imageUri;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public MyLandmark(String id, String landmarkName, String confidence, String location, String imageUri,
                      String latitude, String longitude, String description) {
        this.id = id;
        this.landmarkName = landmarkName;
        this.confidence = confidence;
        this.location = location;
        this.imageUri = imageUri;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
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
        dest.writeString(this.id);
        dest.writeString(this.landmarkName);
        dest.writeString(this.confidence);
        dest.writeString(this.location);
        dest.writeString(this.imageUri);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.description);
    }

    public MyLandmark(Parcel in) {
        this.id = in.readString();
        this.landmarkName = in.readString();
        this.confidence = in.readString();
        this.location = in.readString();
        this.imageUri = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
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
