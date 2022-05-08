package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.models.news.News;

import java.util.ArrayList;

/**
 * {
 *   "location": {
 *     "long": -95.712891,
 *     "countryOrRegion": "United States",
 *     "provinceOrState": null,
 *     "county": null,
 *     "isoCode": "US",
 *     "lat": 37.09024
 *   },
 *   "updatedDateTime": "2020-06-04T02:08:44.5850276Z",
 *   "news": [
 *     {
 *       "path": "_news/2020-06-03-trump-administration-selects-five-coronavirus-vaccine-candidates-as-finalists.md",
 *       "title": "Trump Administration Selects Five Coronavirus Vaccine Candidates as Finalists",
 *       "excerpt": "The White House is eager to project progress, but the public-private partnership it has created still faces scientific hurdles, internal tensions and questions from Congress.",
 *       "heat": 142,
 *       "tags": [
 *         "US"
 *       ],
 *       "type": "article",
 *       "webUrl": "https://www.nytimes.com/2020/06/03/us/politics/coronavirus-vaccine-trump-moderna.html",
 *       "ampWebUrl": "https://www.nytimes.com/2020/06/03/us/politics/coronavirus-vaccine-trump-moderna.amp.html",
 *       "cdnAmpWebUrl": "https://www-nytimes-com.cdn.ampproject.org/c/s/www.nytimes.com/2020/06/03/us/politics/coronavirus-vaccine-trump-moderna.amp.html",
 *       "publishedDateTime": "2020-06-03T10:16:00-07:00",
 *       "updatedDateTime": null,
 *       "provider": {
 *         "name": "New York Times",
 *         "domain": "nytimes.com",
 *         "images": null,
 *         "publishers": null,
 *         "authors": null
 *       },
 *       "images": [
 *         {
 *           "url": "https://static01.nyt.com/images/2020/06/03/us/politics/03dc-virus-vaccine1/03dc-virus-vaccine1-facebookJumbo.jpg",
 *           "width": 1050,
 *           "height": 550,
 *           "title": "Trump Administration Selects Five Coronavirus Vaccine Candidates as Finalists",
 *           "attribution": null
 *         }
 *       ],
 *       "locale": "en-us",
 *       "categories": [
 *         "news"
 *       ],
 *       "topics": [
 *         "Treatment",
 *         "Coronavirus",
 *         "Political Impact",
 *         "Coronavirus in US"
 *       ]
 *     },
 *     {
 *       "path": "_news/2020-06-03-coronavirus-in-louisiana-lousiana-reporting-387-new-cases-35-new-deaths-on-wednesday.md",
 *       "title": "Coronavirus in Louisiana: Lousiana reporting 387 new cases, 35 new deaths on Wednesday",
 *       "excerpt": "The Louisiana Department of Health reported 387 new cases and 35 new deaths on Wednesday, June 3. This brings the stateÔÇÖs total number of cases to",
 *       "heat": 182,
 *       "tags": [
 *         "US",
 *         "US-LA"
 *       ],
 *       "type": "article",
 */

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    Context context;
    ArrayList<News> news;

    public NewsAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_row, parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News current = news.get(position);
        holder.cardArticleImage.setVisibility(View.GONE);

        //Log.i("CustomNews", current.getProvider().getImages());

        // Add image if there is any
        if (current.getImages() != null) {
            Glide.with(context).load(current.getImages().get(0).getUrl()).into(holder.articleImage);
            holder.cardArticleImage.setVisibility(View.VISIBLE);
        }
        holder.textProvider.setText(current.getProvider().getName());
        holder.textTitle.setText(current.getTitle());
        holder.textDescription.setText(current.getExcerpt());

        // Get how long ago article was posted
        int splitIndex = current.getPublishedDateTime().indexOf("T");
        System.out.println(current.getPublishedDateTime());
        String publishedDate = current.getPublishedDateTime().substring(0, splitIndex);
        System.out.println(publishedDate);
        String[] dateDetails = publishedDate.split("-");

        String cleanDate = dateDetails[2] + "/" + dateDetails[1] + "/" + dateDetails[0];

        holder.textHowLong.setText(cleanDate);

        holder.textReadMore.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getWebUrl()));
            context.startActivity(browserIntent);
        });
    }

    public void updateData(ArrayList<News> updatedNews) {
        news.clear();
        news.addAll(updatedNews);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public ImageView articleImage;
        public TextView textProvider;
        public TextView textTitle;
        public TextView textDescription;
        public TextView textReadMore;
        public TextView textHowLong;
        public CardView cardArticleImage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.img_article);
            textProvider = itemView.findViewById(R.id.tv_provider_name);
            textTitle = itemView.findViewById(R.id.tv_article_title);
            textDescription = itemView.findViewById(R.id.tv_article_description);
            textReadMore = itemView.findViewById(R.id.tv_read_more);
            textHowLong = itemView.findViewById(R.id.tv_time_ago);
            cardArticleImage = itemView.findViewById(R.id.card_article_image);
        }
    }
}
