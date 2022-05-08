package com.gamiro.covidjournal.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.gamiro.covidjournal.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class AppUtil {
    // CONSTANTS

    // Notifications actions
    public static final String NOTIFICATION_ACTION = "notificationAction";
    public static final String OPEN_USER_TEST = "openUserTest";
    public static final String OPEN_FRIENDS = "openFriends";
    public static final String ACCEPT_POST = "acceptPost";
    public static final String REMINDER_TEST_PENDING = "openPendingTest";
    public static final String REMINDER_VERIFY_EMAIL = "verifyEmail";

    // Notification types
    public static final String MIGHT_HAVE_CORONA = "mightHaveCorona";
    public static final String HAS_CORONA = "hasCorona";
    public static final String URGENT_NOTIFICATION = "URGENT";

    // Friend status
    public static final String FRIEND_SEND_REQUEST = "sendRequest";
    public static final String FRIEND_PENDING = "pending";
    public static final String FRIEND_ACCEPTED = "friends";

    // Keys for the dates inside date view model
    public static final String ADD_TEST_MADE = "addTestDateMade";
    public static final String ADD_TEST_RESULT = "addTestDateResult";
    public static final String REGISTER_DOB = "registerDob";
    public static final String ADD_ACTIVITY_DATE = "addActivityDate";
    public static final String ADD_PERSON_DATE = "addPersonDate";
    public static final String ADD_OTHER_COUNT = "addOtherCount";
    public static final String TEMPERATURE_INDEX = "temperatureIndex";

    // Integer for nav drawer animation
    public static final float NAVIGATION_END_SCALE = 0.7f;

    // Request code
    public static final String APP_SERVER_CODE = "2102002";

    // Intents for intro + start up
    public static final String MY_PREFS = "myPrefs";
    public static final String COMES_FROM_STARTUP = "comesFromStartup";
    public static final String INTRO_OPENED = "isIntroOpened";

    // Links to website
    public static final String LINK_WEBSITE = "https://www.covidjournal.netlify.app/";
    public static final String LINK_PRIVACY = "https://www.covidjournal.netlify.app/policies/privacy.html";
    public static final String LINK_TERMS = "https://www.covidjournal.netlify.app/policies/terms.html";
    public static final String LINK_FAQ = "https://www.covidjournal.netlify.app/faq.html";

    // Remember email
    public static final String REMEMBER_EMAIL = "rememberEmail";

    // Request codes for location
    public static final int REQUEST_PLACES = 100;
    public static final int REQUEST_PROFILE_IMAGE = 45;

    // Limit to tests
    public static final int MAXIMUM_TESTS = 50;

    // ISO codes
    public static final String[] ISO_COUNTRY_CODES = Locale.getISOCountries();

    // Countries with issues
    public static final String[] COUNTRIES_PICKER = {
            "Saint Vincent and the Grenadines",
            "Faroe Islands",
            "Aruba Sint Maarten Curacao",
            "Turks and Caicos Islands",
            "Sao Tome And Principe",
            "Congo",
            "Saint Pierre and Miquelon",
            "Democratic Republic of Congo",
            "South Korea",
            "Central African Republic",
            "United Kingdom",
            "United Arab Emirates",
            "Saint Kitts and Nevis",
            "St. Barth"
    };

    public static final String[] COUNTRIES_API = {
            "St. Vincent Grenadines",
            "Faeroe Islands",
            "Caribbean Netherlands",
            "Turks and Caicos",
            "Sao Tome and Principe",
            "Congo",
            "Saint Pierre Miquelon",
            "DRC",
            "S. Korea",
            "CAR",
            "UK",
            "UAE",
            "Saint Kitts and Nevis",
            "St. Barth"
    };

    public static final String[] COUNTRIES_FLAG = {
            "St Vincent and Grenadines",
            "",
            "Netherlands Antilles",
            "",
            "São Tomé and Príncipe",
            "Congo Republic",
            "Saint Pierre and Miquelon",
            "DR Congo",
            "",
            "",
            "",
            "",
            "St Kitts and Nevis",
            "Saint Barthélemy"
    };

    public static final String[] COUNTRIES_LOCALE_CODES = {
            "VC",
            "FO",
            "BQ",
            "TC",
            "ST",
            "CG",
            "PM",
            "CD",
            "KR",
            "CF",
            "GB",
            "AE",
            "KN",
            "BL"
    };

    /**
     * UTILITY
     */
    public static String getCountryCode(String countryName) {
        for (String code : ISO_COUNTRY_CODES) {
            Locale locale = new Locale("", code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code;
            }
        }
        return "";
    }
    public static int getFlagCountryId(String countryName) {
        final Country countryObject = World.getCountryFrom(countryName);
        return countryObject.getFlagResource();
    }
    public static int getFlagFromLocale(String code) {
        return World.getFlagOf(code);
    }
    public static String getApiCountryFromPicker(String api) {
        List<String> listApiCountries = Arrays.asList(AppUtil.COUNTRIES_PICKER);
        if (listApiCountries.contains(api)) {
            int index = listApiCountries.indexOf(api);
            return AppUtil.COUNTRIES_API[index];
        }

        return api;
    }
    public static String getPickerCountryFromApi(String picker) {
        List<String> listApiCountries = Arrays.asList(AppUtil.COUNTRIES_API);
        if (listApiCountries.contains(picker)) {
            int index = listApiCountries.indexOf(picker);
            return AppUtil.COUNTRIES_PICKER[index];
        }

        return picker;
    }
    public static String getCodeFromPicker(String picker) {
        List<String> listApiCountries = Arrays.asList(AppUtil.COUNTRIES_PICKER);
        if (listApiCountries.contains(picker)) {
            int index = listApiCountries.indexOf(picker);
            return AppUtil.COUNTRIES_LOCALE_CODES[index];
        }

        Log.i("Code", "getCodeFromPicker: " + getCountryCode(picker));
        return getCountryCode(picker);
    }
    public static int getFlagFromApiCountry(String countryName) {
        String localeCountry = "";
        List<String> listApiCountries = Arrays.asList(AppUtil.COUNTRIES_API);
        if (listApiCountries.contains(countryName)) {
            int index = listApiCountries.indexOf(countryName);
            localeCountry = AppUtil.COUNTRIES_FLAG[index];
        }
        System.out.println(localeCountry);
        return getFlagCountryId(localeCountry);
    }
    public static int getFlagFromPickerCountry(String countryName) {
        String localeCode = "";
        List<String> listApiCountries = Arrays.asList(AppUtil.COUNTRIES_PICKER);
        if (listApiCountries.contains(countryName)) {
            int index = listApiCountries.indexOf(countryName);
            localeCode = AppUtil.COUNTRIES_LOCALE_CODES[index];
            return getFlagFromLocale(localeCode);
        }
        return getFlagCountryId(countryName);
    }

    /**
     * Returns boolean from shared preferences
     * @param context: getApplicationContext()
     * @return boolean
     */
    // boolean
    public static boolean restoreBooleanPreferenceData(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
    public static void saveBooleanPreferenceData(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(AppUtil.MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    // String
    public static String restoreStringPreferenceData(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
    public static void saveStringPreferenceData(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(AppUtil.MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Adds a view to recycler views when empty
     * The recycler view needs to be wrapped in a constraint layout
     * @param parent: get it with getView() inside fragment
     * @return the layout for empty recycler
     */
    public static LinearLayout addEmptyViewRecyclerView(View parent, int topViewId, String text) {
        if (parent == null)
            return null;
        if (!(parent instanceof ViewGroup))
            return null;

        ViewGroup viewGroup = (ViewGroup) parent;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View popup = inflater.inflate(R.layout.empty_recycler, viewGroup, false);

        if (!text.isEmpty()) {
            TextView textView = popup.findViewById(R.id.tv_empty_recycler);
            textView.setText(text);
        }

        viewGroup.addView(popup);

        // Set constraints
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) parent);

        constraintSet.connect(
                R.id.empty_container, ConstraintSet.RIGHT,
                parent.getId(), ConstraintSet.RIGHT,0);

        // If no text up, constrain to the layout itself
        if (topViewId != parent.getId()) {
            constraintSet.connect(
                    R.id.empty_container, ConstraintSet.TOP,
                    topViewId, ConstraintSet.BOTTOM, 16);
        } else {
            constraintSet.connect(
                    R.id.empty_container, ConstraintSet.TOP,
                    topViewId, ConstraintSet.TOP, 16
            );
        }
        constraintSet.connect(
                R.id.empty_container, ConstraintSet.BOTTOM,
                parent.getId(), ConstraintSet.BOTTOM, 16
        );

        constraintSet.connect(
                R.id.empty_container, ConstraintSet.LEFT,
                parent.getId(), ConstraintSet.LEFT,0);

        constraintSet.applyTo((ConstraintLayout) parent);

        return (LinearLayout) popup;
    }

    /**
     * Generates the current date and time in the format: yyyy-mm-dd hh:mm:ss
     * @return date
     */
    public static String getCurrentTimeDate() {
        String dateTime = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        } else {
            // Generate random id, as date is deprecated and there is no other way
            dateTime = UUID.randomUUID().toString();;
        }
        return dateTime;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getToday() {
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateDifferenceDays(String start, String end) {
        Date date1;
        Date date2;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date1 = format.parse(start);
            date2 = format.parse(end);
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            return Long.toString(differenceDates);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getAge(String dob) {
        Date date1;
        Date date2;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date1 = format.parse(getToday());
            date2 = format.parse(dob);
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = (long) (difference / (24 * 60 * 60 * 1000 * 365.25));
            return Long.toString(differenceDates);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Converts camel case to human readable text
     * @param s: CamelCase string
     * @return Human readable string
     */
    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    /**
     * Returns correct pronoun for gender
     * @param gender
     * @return him, her or them
     */
    public static String getPronoun(String gender) {
        String pronoun = "him";
        if (gender.toLowerCase().equals("female")) {
            pronoun = "her";
        } else if (gender.toLowerCase().equals("other")) {
            pronoun = "them";
        }
        return pronoun;
    }

    /**
     * Returns a string like this: 12ab,2345b
     * @param arrayList -
     * @return string
     */
    public static String listToString(ArrayList<String> arrayList) {
        StringBuilder result = new StringBuilder();

        for (String item: arrayList) {
            result.append(item);
            result.append(" ");
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }
    public static ArrayList<String> stringToList(String string, String delimiter) {
        return new ArrayList<String>(Arrays.asList(string.split(delimiter)));
    }

    /**
     * Returns index of element inside array
     * @param array - int[]
     * @param element - int
     * @return index
     */
    public static int indexOf(int[] array, int element) {
        List<Integer> intList = new ArrayList<Integer>(array.length);
        for (int i : array) {
            intList.add(i);
        }

        return intList.indexOf(element);
    }

    /**
     * Notification responses
     * title + body
     */
    public static String getTitleFromNotificationType(String notificationType, String userName) {
        String title = "";

        switch (notificationType) {
            case MIGHT_HAVE_CORONA:
                title = "Contact might have coronavirus";
                break;
            case HAS_CORONA:
                title = "Contact has coronavirus";
                break;
            case FRIEND_ACCEPTED:
                title = "You and " + userName + " are friends now";
                break;
            case OPEN_FRIENDS:
                title = userName + " has sent you a friend request!";
                break;
            default:
                break;
        }

        return title;
    }
    public static String getBodyFromNotificationType(String notificationType, String userName) {
        String body = "";

        switch (notificationType) {
            case MIGHT_HAVE_CORONA:
                body = userName + " might have it. Order a test!";
                break;
            case HAS_CORONA:
                body = userName + " has been tested positive. Order a test and self-isolate!";;
                break;
            case FRIEND_ACCEPTED:
                body = userName + " has accepted your friend request";
                break;
            case OPEN_FRIENDS:
                body = "Click the notification and accept or reject the friend request";
                break;
            default:
                break;
        }

        return body;
    }

    /**
     * Celsius -> fahrenheit and vice versa
     * @param celsius -
     * @return -
     */
    public static double celsiusToFahrenheit(double celsius) {
        return 9 * celsius / 5 + 32;
    }
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }
}
