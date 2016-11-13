package com.example.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Adapter to show list of news articles
 * Created by carlos on 11/12/16.
 */
class NewsAdapter extends ArrayAdapter<NewsArticle> {

    NewsAdapter(Context context, ArrayList<NewsArticle> newsArticlesList) {
        super(context, 0, newsArticlesList);
    }

    @NonNull
    @Override
    public View getView(int position, View itemView, @NonNull ViewGroup parent) {
        if (itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_news_article,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtTitle = (TextView) itemView.findViewById(R.id.title_textview);
            viewHolder.txtSection = (TextView) itemView.findViewById(R.id.section_name_textview);
            viewHolder.imageThumbnail = (ImageView) itemView.findViewById(R.id.list_item_imageview);
            itemView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) itemView.getTag();
        NewsArticle article = getItem(position);

        if (article != null) {
            holder.txtTitle.setText(article.getTitle());
            holder.txtSection.setText(article.getSection());
            Glide.with(holder.imageThumbnail.getContext()).load(article.getImage()).into(holder.imageThumbnail);
            holder.imageThumbnail.setContentDescription(article.getTitle());
        }
        return itemView;
    }

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSection;
        ImageView imageThumbnail;
    }
}
