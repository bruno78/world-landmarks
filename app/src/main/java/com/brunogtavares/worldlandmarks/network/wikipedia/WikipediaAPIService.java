package com.brunogtavares.worldlandmarks.network.wikipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by brunogtavares on 10/15/18.
 */

public interface WikipediaAPIService {

    @GET("api.php?format=json&action=query&prop=extracts&explaintext=1&titles=")
    Call<WikiEntry> getInfo(@Query("placeName") String placeName);
}
