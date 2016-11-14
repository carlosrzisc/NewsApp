package com.example.newsapp;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader for news articles
 * Created by carlos on 11/13/16.
 */

class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {
    private static final String LOG_TAG = NewsLoader.class.getName();
    private String searchText;

    NewsLoader(Context context, String searchText) {
        super(context);
        this.searchText = searchText;
    }

    @Override
    public List<NewsArticle> loadInBackground() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String articleJsonStr = null;
        String apiKey = "test";
        String showFields = "thumbnail";

        try {
            final String BASE_URL = "http://content.guardianapis.com/search?";
            final String QUERY_PARAM = "q";
            final String API_KEY = "api-key";
            final String SHOW_FIELDS = "show-fields";

            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SHOW_FIELDS, showFields)
                    .appendQueryParameter(API_KEY, apiKey);
            if (!searchText.isEmpty()) {
                uriBuilder.appendQueryParameter(QUERY_PARAM, searchText);
            }
            Uri builtUri = uriBuilder.build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            articleJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getArticlesFromJson(articleJsonStr, 20);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private ArrayList<NewsArticle> getArticlesFromJson(String articlesJsonStr, int maxResults)
            throws JSONException {

        final String RESPONSE = "response";
        final String RESULTS = "results";
        final String SECTION_NAME = "sectionName";
        final String WEB_TITLE = "webTitle";
        final String FIELDS = "fields";
        final String THUMBNAIL = "thumbnail";
        final String WEB_URL = "webUrl";

        int numArticles;

        ArrayList<NewsArticle> newsArticles = new ArrayList<>();

        try {
            JSONObject articlesJson = new JSONObject(articlesJsonStr);
            JSONObject responseJson = articlesJson.getJSONObject(RESPONSE);
            JSONArray resultsJsonArray = responseJson.getJSONArray(RESULTS);

            if (resultsJsonArray.length() > maxResults) {
                numArticles = maxResults;
            } else {
                numArticles = resultsJsonArray.length();
            }

            for (int i = 0; i < numArticles; i++) {
                String title;
                String sectionName;
                String webUrl;
                String thumbnail;

                try {
                    JSONObject articleJson = resultsJsonArray.getJSONObject(i);
                    title = articleJson.getString(WEB_TITLE);
                    sectionName = articleJson.getString(SECTION_NAME);
                    webUrl = articleJson.getString(WEB_URL);
                    JSONObject fieldsJson = articleJson.getJSONObject(FIELDS);
                    thumbnail = fieldsJson.getString(THUMBNAIL);

                    newsArticles.add(new NewsArticle(title, sectionName, thumbnail, webUrl));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Missing field", e);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON exception", e);
        }
        return newsArticles;
    }
}
