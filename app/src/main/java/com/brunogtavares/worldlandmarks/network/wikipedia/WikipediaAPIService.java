package com.brunogtavares.worldlandmarks.network.wikipedia;

import com.brunogtavares.worldlandmarks.model.WikiEntry;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by brunogtavares on 10/15/18.
 */

public interface WikipediaAPIService {
    // "api.php?format=json&action=query&prop=extracts&explaintext=1&titles="

    @GET("api.php?format=json&action=query&prop=extracts&explaintext=1")
    Call<WikiEntry> getInfo(@Query("titles") String placeName);
}
