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

    public WikiEntry() {}

    public WikiEntry(String title, String extract) {
        this.title = title;
        this.description = extract;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
