package com.brunogtavares.worldlandmarks.network;

import com.brunogtavares.worldlandmarks.network.wikipedia.WikiEntry;
import com.brunogtavares.worldlandmarks.network.wikipedia.WikipediaAPIClient;
import com.brunogtavares.worldlandmarks.network.wikipedia.WikipediaAPIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by brunogtavares on 10/15/18.
 */

public class Repository {

    private static final Object LOCK = new Object();
    private static Repository sInstance;

    private static WikipediaAPIService mService;

    private Repository(WikipediaAPIService service) {
        this.mService = service;
    }

    public static Repository getsInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                WikipediaAPIService service = WikipediaAPIClient.getRetrofitInstance()
                        .create(WikipediaAPIService.class);

            }
        }
        return sInstance;
    }

    public WikiEntry getInfo(String placeName) {
        final WikiEntry[] wikiEntry = new WikiEntry[1];

        Call<WikiEntry> call = mService.getInfo(placeName);
        call.enqueue(new Callback<WikiEntry>() {
            @Override
            public void onResponse(Call<WikiEntry> call, Response<WikiEntry> response) {
                wikiEntry[0] = response.body();
            }

            @Override
            public void onFailure(Call<WikiEntry> call, Throwable t) {
                Timber.d("Unable to fetch data: " + t);
            }
        });

        return wikiEntry[0];
    }
}
