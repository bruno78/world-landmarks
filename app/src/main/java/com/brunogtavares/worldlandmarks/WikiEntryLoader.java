package com.brunogtavares.worldlandmarks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;

import com.brunogtavares.worldlandmarks.model.WikiEntry;
import com.brunogtavares.worldlandmarks.utils.WikipediaAPIUtils;

import java.io.IOException;

/**
 * Created by brunogtavares on 10/27/18.
 */

public class WikiEntryLoader extends AsyncTaskLoader<WikiEntry> {

    private String mQuery;

    public WikiEntryLoader(@NonNull Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public WikiEntry loadInBackground() {

        if(mQuery == null) return null;
        WikiEntry wikiEntry = new WikiEntry();

        try {
            wikiEntry = WikipediaAPIUtils.extractWikiEntry(mQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wikiEntry;
    }
}
