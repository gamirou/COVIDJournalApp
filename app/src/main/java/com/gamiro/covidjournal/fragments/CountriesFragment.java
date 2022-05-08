package com.gamiro.covidjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.CountriesAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.countries.CountryStatistics;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CountriesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CountriesFragment";
    private ArrayList<CountryStatistics> countriesDataList;
    private ArrayList<String> countriesList;

    // View model
    private HomeActivityViewModel viewModel;

    // Layout elements
    private RecyclerView recyclerCountries;
    private CountriesAdapter countriesAdapter;
    private EditText editSearchBox;
    private Spinner spinnerFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        editSearchBox = getView().findViewById(R.id.search_country);
        recyclerCountries = getView().findViewById(R.id.recycler_countries);
        spinnerFilter = getView().findViewById(R.id.spinner_filter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filters_countries, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        recyclerCountries.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Listen for clicks
        spinnerFilter.setOnItemSelectedListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).setProgressState(true);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        // Convert to JSON
        countriesDataList = new ArrayList<>();
        countriesList = new ArrayList<>();

        countriesAdapter = new CountriesAdapter(getActivity(), countriesList, countriesDataList);
        recyclerCountries.setAdapter(countriesAdapter);

        viewModel.getCoronaData().observe(getViewLifecycleOwner(), stringCountryStatisticsHashMap ->  {
            // Get data
            countriesDataList.clear();
            countriesList.clear();

            countriesDataList.addAll(stringCountryStatisticsHashMap.values());
            countriesList = new ArrayList<>(stringCountryStatisticsHashMap.keySet());

//            for (String key: stringCountryStatisticsHashMap.keySet()) {
//                int flagId = AppUtil.getFlagCountryId(AppUtil.getPickerCountryFromApi(key));
//                if (flagId == 0) {
//                    flagId = AppUtil.getFlagFromApiCountry(key);
//                }
//                //Log.i(TAG, "country = " + country + " and flag id = " + flagId);
//
//                if (flagId == 0) {
//                    countriesList.add(key);
//                    countriesDataList.add(stringCountryStatisticsHashMap.get(key));
//                }
//            }

            sortBy("ascending", "cases");
            spinnerFilter.setSelection(0);
            countriesAdapter.notifyDataSetChanged();
//            countriesAdapter.resetExpandedValues();

            ((HomeActivity) getActivity()).setProgressState(false);
        });

//        for (String code: AppUtil.ISO_COUNTRY_CODES) {
//            Locale locale = new Locale("", code);
//            Log.i("C-" + code, code + " = " + locale.getDisplayCountry());
//        }

        // Search box
        editSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                findResultBySearch(editable.toString());
            }
        });
    }

    // Search
    private void findResultBySearch(String query) {
        ArrayList<CountryStatistics> filteredList = new ArrayList<>();
        ArrayList<String> filteredCountries = new ArrayList<>();

        for (CountryStatistics statsCountry : countriesDataList) {
            // Find the name of the country
            String country = statsCountry.getCountry();

            // Add the country if in search box
            if (country.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(statsCountry);
                filteredCountries.add(country);
            }
        }

        countriesAdapter.filter(filteredList, filteredCountries);
    }

    // Sort
    private void sortBy(final String order, final String option) {
        if (!option.equals("mortalityRate")) {
            // Sort by the number of cases
            Collections.sort(countriesDataList, (Comparator<CountryStatistics>) (statistics, t1) -> {
                Integer value1 = 0;
                Integer value2 = 0;

                switch (option) {
                    case "cases":
                        value1 = statistics.getCases();
                        value2 = t1.getCases();
                        break;

                    case "deaths":
                        value1 = statistics.getDeaths();
                        value2 = t1.getDeaths();
                        break;

                    case "active":
                        value1 = statistics.getActive();
                        value2 = t1.getActive();
                        break;

                    case "recovered":
                        value1 = statistics.getRecovered();
                        value2 = t1.getRecovered();
                        break;

                    case "totalTests":
                        value1 = statistics.getTotalTests();
                        value2 = t1.getTotalTests();
                        break;

                    case "critical":
                        value1 = statistics.getCritical();
                        value2 = t1.getCritical();
                        break;

                    case "todayCases":
                        value1 = statistics.getTodayCases();
                        value2 = t1.getTodayCases();
                        break;

                    case "todayDeaths":
                        value1 = statistics.getTodayDeaths();
                        value2 = t1.getTodayDeaths();
                        break;

                    case "casesPerOneMillion":
                        value1 = statistics.getCasesPerOneMillion();
                        value2 = t1.getCasesPerOneMillion();
                        break;
                }

                if (value1 != null && value2 != null) {
                    if (value1 != -1 && value2 != -1) {
                        if (order.equals("ascending")) {
                            return value2 - value1;
                        } else {
                            return value1 - value2;
                        }
                    }
                }
                return 0;
            });
        } else {
            // Mortality rate is computed by the app, not given by the api
            Collections.sort(countriesDataList, (Comparator<CountryStatistics>) (statistics, t1) -> {

                double mortalityRate1 = (double) statistics.getDeaths() / statistics.getCases() * 100.0;
                double mortalityRate2 = (double) t1.getDeaths() / t1.getCases() * 100.0;
                double difference = mortalityRate1 - mortalityRate2;

                if (order.equals("descending")) {
                    difference = -difference;
                }

                if(difference > 0.00001) return -1;
                if(difference < -0.00001) return 1;

                return 0;
            });
        }

        if (countriesList.size() != 0) {
            countriesList.clear();
        }
        for (CountryStatistics statistics : countriesDataList) {
            countriesList.add(statistics.getCountry());
        }

        if (countriesAdapter != null) {
            countriesAdapter.filter(countriesDataList, countriesList);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String itemSelected = adapterView.getItemAtPosition(i).toString().toLowerCase();
        String[] theWords = itemSelected.split(" ");

        // The two parameters for the sort by are order and option
        String order = "";
        if (theWords[0].equals("most") || theWords[0].equals("largest")) {
            order = "ascending";
        } else if (theWords[0].equals("least") || theWords[0].equals("lowest")) {
            order = "descending";
        }

        String option = theWords[1];
        if (theWords[1].equals("tests")) {
            option = "totalTests";
        } else if (theWords[1].equals("mortality")) {
            option = "mortalityRate";
        }

        if (theWords.length == 3) {
            if (!theWords[1].equals("active")) {
                if (theWords[2].equals("cases")) {
                    option = "todayCases";
                } else if (theWords[2].equals("deaths")) {
                    option = "todayDeaths";
                }
            }
        } else if (theWords.length == 4) {
            option = "casesPerOneMillion";
        }

        System.out.println(order);
        System.out.println(option);

        sortBy(order, option);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
