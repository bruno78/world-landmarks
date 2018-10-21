package com.brunogtavares.worldlandmarks.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brunogtavares on 10/15/18.
 */

public class WikiEntry {

    @SerializedName("title")
    private String title;
    @SerializedName("extract")
    private String description;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
