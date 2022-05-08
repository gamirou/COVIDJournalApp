package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.countries.CountryStatistics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {

    private static final String TAG = "CountriesAdapter";

    private Context context;
    private ArrayList<CountryStatistics> coronaData;
    private ArrayList<String> countries;
    private ArrayList<Boolean> expandedArray;

    public CountriesAdapter(Context ct, ArrayList<String> _countries, ArrayList<CountryStatistics> _coronaData) {
        countries = _countries;
        coronaData = _coronaData;
        context = ct;
//        expandedArray = new ArrayList<>(_countries.size());
//        for (int i = 0; i < expandedArray.size(); i++) {
//            expandedArray.add(false);
//        }
    }

    @NonNull
    @Override
    public CountriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.country_row, parent,false);
        return new CountriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CountriesViewHolder holder, int position) {
        String country = countries.get(position);
        CountryStatistics countryStatistics = coronaData.get(position);

        holder.textCountryName.setText(country);
        if (country.contains("Channel")) {
            country = "UK";
        }
        if (country.contains("World")) {
            holder.imageFlag.setImageResource(AppUtil.getFlagFromLocale(""));
        } else {
            int flagId = AppUtil.getFlagCountryId(AppUtil.getPickerCountryFromApi(country));
            if (flagId == 0) {
                flagId = AppUtil.getFlagFromApiCountry(country);
            }
            holder.imageFlag.setImageResource(flagId);
        }

        // Stats

        int deaths = countryStatistics.getDeaths();
        int cases = countryStatistics.getCases();

        holder.textActiveCases.setText(countryStatistics.getActive() != null ? countryStatistics.getActive()+"" : "-");
        holder.textConfirmedCases.setText(cases != -1 ? cases+"" : "-");
        holder.textRecovered.setText(countryStatistics.getRecovered() != null ? countryStatistics.getRecovered()+"" : "-");
        holder.textDeaths.setText(deaths != -1 ? deaths+"" : "-");
        holder.textTests.setText(countryStatistics.getTotalTests() != null ? countryStatistics.getTotalTests()+"" : "-");
        holder.textTodayCases.setText(countryStatistics.getTodayCases() != null ? countryStatistics.getTodayCases()+"" : "-");
        holder.textTodayDeaths.setText(countryStatistics.getTodayDeaths() != null ? countryStatistics.getTodayDeaths()+"" : "-");
        holder.textCasesMillion.setText(countryStatistics.getCasesPerOneMillion() != null ? countryStatistics.getCasesPerOneMillion()+"" : "-");
        holder.textCritical.setText(countryStatistics.getCritical() != null ? countryStatistics.getCritical()+"" : "-");


        // Mortality rate
        if (deaths != -1 && cases != -1) {
            double mortalityRate = (double) deaths / cases * 100.0;
            double percMortality = (double) Math.round(mortalityRate * 100.0) / 100.0;
            holder.textMortality.setText(String.valueOf(percMortality) + "%");
        } else {
            holder.textMortality.setText("-");
        }

//        Log.d(TAG, "onBindViewHolder: (" + position + ") - isExpanded = " + holder.isExpanded);
//        holder.expandButton.setOnClickListener(view -> {
//            holder.collapseExpandContent();
//            Log.d(TAG, "onBindViewHolder: " + position);
//            Log.d(TAG, "onBindViewHolder: " + holder.isExpanded);
//        });

        holder.expandButton.setOnClickListener(view -> {
//            boolean isExpanded = expandedArray.get(position);
//            expandedArray.set(position, !isExpanded);
//            holder.collapseExpandContent(isExpanded);
            holder.collapseExpandContent();
        });
    }

    @Override
    public int getItemCount() {
        return coronaData.size();
    }

//    public void resetExpandedValues() {
//        expandedArray.clear();
//        for (int i = 0; i < countries.size(); i++) {
//            expandedArray.add(false);
//        }
//    }

    public void filter(ArrayList<CountryStatistics> newCorona, ArrayList<String> newCountries) {
        coronaData = newCorona;
        countries = newCountries;
//        resetExpandedValues();
        notifyDataSetChanged();
    }

    public class CountriesViewHolder extends RecyclerView.ViewHolder {

        // Layout elements
        TextView textCountryName;
        ImageView imageFlag;
        TextView textConfirmedCases, textActiveCases, textRecovered, textDeaths, textTests;
        TextView textTodayCases, textTodayDeaths, textCritical, textCasesMillion, textMortality;

        ConstraintLayout expandContent;
        Button expandButton;
        CardView cardContainer;

        boolean isExpanded = false;

        public CountriesViewHolder(@NonNull View itemView) {
            super(itemView);

            textCountryName = itemView.findViewById(R.id.hub_country_name);

            // Left half
            textConfirmedCases = itemView.findViewById(R.id.hub_confirmed_cases);
            textActiveCases = itemView.findViewById(R.id.hub_active_cases);
            textRecovered = itemView.findViewById(R.id.hub_recovered);
            textDeaths = itemView.findViewById(R.id.hub_deceased);
            textTests = itemView.findViewById(R.id.hub_tests);

            // Right half
            textTodayCases = itemView.findViewById(R.id.hub_today_cases);
            textTodayDeaths = itemView.findViewById(R.id.hub_today_deaths);
            textCritical = itemView.findViewById(R.id.hub_critical);
            textCasesMillion = itemView.findViewById(R.id.hub_cases_million);
            textMortality = itemView.findViewById(R.id.hub_mortality_rate);

            expandContent = itemView.findViewById(R.id.expand_content);
            expandButton = itemView.findViewById(R.id.expand_button);
            cardContainer = itemView.findViewById(R.id.card_container);

            imageFlag = itemView.findViewById(R.id.hub_flag);

            expandContent.setVisibility(View.GONE);
        }

        // TODO: Fix this, others are opening at the same time
        private void collapseExpandContent(/*boolean isExpanded*/) {
            this.isExpanded = !this.isExpanded;

            if (isExpanded) {
                // it's collapsed - expand it
                TransitionManager.beginDelayedTransition(cardContainer, new AutoTransition());

                expandContent.setVisibility(View.VISIBLE);
                expandButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            } else {
                // it's expanded - collapse it
                TransitionManager.beginDelayedTransition(cardContainer, new AutoTransition());

                expandContent.setVisibility(View.GONE);
                expandButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }
        }
    }
}
