package com.brunogtavares.worldlandmarks.network.wikipedia;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by brunogtavares on 10/15/18.
 * This is the wikipedia api client based on this full url
 * https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&explaintext=1&titles=
 */

public class WikipediaAPIClient {

    private static final Object LOCK = new Object();
    private static Retrofit sInstance;
    private static final String BASE_URL =
            "https://en.wikipedia.org/w/";

    public static Retrofit getRetrofitInstance() {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return sInstance;

    }
}
