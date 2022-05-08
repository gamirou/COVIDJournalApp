package com.gamiro.covidjournal.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.NewsAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.news.News;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    // Data for news
    private HomeActivityViewModel viewModel;
    private ArrayList<News> newsArticlesList;

    // Layout elements
    private RecyclerView recyclerNews;
    private NewsAdapter newsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerNews = getView().findViewById(R.id.recycler_news);
        recyclerNews.setLayoutManager(new LinearLayoutManager(getActivity()));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        ((HomeActivity) requireActivity()).setProgressState(true);

        newsArticlesList = new ArrayList<>();

        newsAdapter = new NewsAdapter(getActivity(), newsArticlesList);
        recyclerNews.setAdapter(newsAdapter);

        LinearLayout emptyContainer = AppUtil.addEmptyViewRecyclerView(
                getView().findViewById(R.id.news_container),
                R.id.news_container, ""
        );

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stop fetching after 8 seconds
                if (newsArticlesList.size() == 0) {
                    ((HomeActivity) getActivity()).setProgressState(false);
                    emptyContainer.setVisibility(View.VISIBLE);
                }
            }
        }, 8000);

        viewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            viewModel.setUserCountry(userData.getCountry());
        });

        // Get news
        viewModel.getCoronaNews().observe(getViewLifecycleOwner(), news -> {
            newsArticlesList = new ArrayList<>(news);
            newsAdapter.updateData(newsArticlesList);
            newsAdapter.notifyDataSetChanged();

            emptyContainer.setVisibility(newsArticlesList.size() == 0 ? View.VISIBLE : View.GONE);

            ((HomeActivity) getActivity()).setProgressState(false);
        });
    }

}