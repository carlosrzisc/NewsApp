package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * News Fragment
 * Created by carlos on 11/12/16.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>>{
    private static final int NEWS_LOADER = 101;
    private NewsAdapter mNewsAdapter;
    EditText txtSearch;

    public NewsFragment() { }

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
                    refreshArticles();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArticles();
    }

    private void initArticles() {
        if (isConnected()) {
            getLoaderManager().initLoader(NEWS_LOADER, null, this);
        } else {
            Toast.makeText(getActivity(), R.string.text_no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void refreshArticles() {
        if (isConnected()) {
            getLoaderManager().restartLoader(NEWS_LOADER, null, this);
        } else {
            Toast.makeText(getActivity(), R.string.text_no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(getActivity(), txtSearch.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> data) {
        mNewsAdapter.clear();
        if (data != null && !data.isEmpty()) {
            mNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mNewsAdapter.clear();
    }
}
