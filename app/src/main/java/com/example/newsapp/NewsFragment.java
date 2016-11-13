package com.example.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

/**
 * News Fragment
 * Created by carlos on 11/12/16.
 */
public class NewsFragment extends Fragment {
    private NewsAdapter mNewsAdapter;
    EditText txtSearch;

    public NewsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mNewsAdapter = new NewsAdapter(getActivity(), new ArrayList<NewsArticle>());

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_news);
        listView.setAdapter(mNewsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsArticle newsArticle = mNewsAdapter.getItem(i);
                if (newsArticle != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsArticle.getUrl()));
                    startActivity(intent);
                }
            }
        });

        txtSearch = (EditText) rootView.findViewById(R.id.input_search);

        Button btnSearch = (Button) rootView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtSearch.getText().toString().equalsIgnoreCase("")) {
                    updateArticles(txtSearch.getText().toString());
                }
            }
        });

        return rootView;
    }

    private void updateArticles(String searchCriteria) {
        new FetchArticleTask().execute(searchCriteria);
    }

    public class FetchArticleTask extends AsyncTask<String, Void, ArrayList<NewsArticle>> {

        private final String LOG_TAG = FetchArticleTask.class.getSimpleName();

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

        @Override
        protected ArrayList<NewsArticle> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

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

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(SHOW_FIELDS, showFields)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

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
        protected void onPostExecute(ArrayList<NewsArticle> newsArticles) {
            if (newsArticles != null) {
                mNewsAdapter.clear();
                for (NewsArticle newsArticle : newsArticles) {
                    mNewsAdapter.add(newsArticle);
                }
            }
        }
    }
}
