package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.models.IntroScreenItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class IntroViewPagerAdapter extends PagerAdapter {

    Context context;
    List<IntroScreenItem> listIntroItems;

    public IntroViewPagerAdapter(Context context, List<IntroScreenItem> listIntroItems) {
        this.context = context;
        this.listIntroItems = listIntroItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.intro_layout, null);

        ImageView introImage = view.findViewById(R.id.intro_image);
        TextView introTitle = view.findViewById(R.id.intro_title);
        TextView introDescription = view.findViewById(R.id.intro_description);

        introImage.setImageResource(listIntroItems.get(position).getImageId());
        introTitle.setText(listIntroItems.get(position).getTitle());
        introDescription.setText(listIntroItems.get(position).getDescription());

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return listIntroItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(container);
    }
}
