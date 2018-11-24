package com.brunogtavares.worldlandmarks.utils;

import com.brunogtavares.worldlandmarks.model.WikiEntry;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by brunogtavares on 10/27/18.
 * The Wikipedia API Utils takes care of making HTTP request and extracting data from API using helper methods:
 * 1. createUrl takes a Url string and converts into URL object
 * 2. makeHTTPRequest takes url and makes a network request
 * 3. readFromInputStream if connection is successful, it takes the results as an input stream and returns
 * an output as JSON string
 * 4. extratFeaturefromJSON takes the results as JSON string and parse it returning a list of movies and
 * its features.
 */

public class WikipediaAPIUtils {
    private static String BASE_URL =
            "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exintro=&explaintext=1&titles=";

    private WikipediaAPIUtils(){}

    public static WikiEntry extractWikiEntry(String query) throws IOException {
        URL urlRequest = new URL(BASE_URL + query);
        String responseString = makeHTTPRequest(urlRequest);

        return getWikiEntryFromResponse(responseString);
    }

    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            }
            else {
                Timber.d("Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Timber.d( "Problem retrieving wikipedia JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    private static WikiEntry getWikiEntryFromResponse(String jsonResponse) {
        JsonElement pageNumber = null;
        JsonElement extract = null;
        JsonElement title = null;
        WikiEntry wikiEntry = new WikiEntry();

        JsonElement jsonElement = new JsonParser().parse(jsonResponse);

        if(jsonElement != null) {
            JsonElement pages = jsonElement.getAsJsonObject().get("query").getAsJsonObject().get("pages");
            Set<Map.Entry<String, JsonElement>> entrySet = pages.getAsJsonObject().entrySet();

            for(Map.Entry<String,JsonElement> entry : entrySet){
                pageNumber = entry.getValue();
            }

            if(pageNumber != null) {
                title =  pageNumber.getAsJsonObject().get("title");
                extract = pageNumber.getAsJsonObject().get("extract");
            }

            if(title != null) wikiEntry.setTitle(title.getAsString());
            if(extract != null) wikiEntry.setDescription(extract.getAsString());
        }

        return wikiEntry;
    }

}
