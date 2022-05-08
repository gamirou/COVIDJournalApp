package com.gamiro.covidjournal.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CityModel.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {
    private static final String TAG = "CityDatabase";

    public abstract CityDao cityDao();
    public static CityDatabase instance;

    public static synchronized CityDatabase getInstance(Context context) {
        if (instance == null) {
            Log.i(TAG, "getInstance: instance is null");
            
            instance = Room.databaseBuilder(
                    context.getApplicationContext(), CityDatabase.class, "city_database"
            ).addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    Log.d(TAG, "addCallback -> onCreate: success");

                    try {
                        InputStream is = context.getApplicationContext().getAssets().open("countriesToCities.json");
                        new LoadCitiesAsyncTask(instance).execute(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);

                    Log.d(TAG, "addCallback -> onOpen: called");
                }
            }).build();
        }
        return instance;
    }

    // Async tasks
    private static class LoadCitiesAsyncTask extends AsyncTask<InputStream, Void, Void> {
        private CityDao cityDao;

        public LoadCitiesAsyncTask(CityDatabase db) {
            this.cityDao = db.cityDao();
        }

        @Override
        protected Void doInBackground(InputStream... inputStreams) {
            String data = null;
            InputStream is = inputStreams[0];

            Log.i(TAG, "doInBackground: success");

            try {
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                data = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }

            JSONObject json = null;
            try {
                json = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Log.i(TAG, "doInBackground: key = " + key);
                try {
                    String citiesResult = json.getString(key);
                    citiesResult = citiesResult.replace("[", "");
                    citiesResult = citiesResult.replace("]", "");
                    citiesResult = citiesResult.replace("\"", "");

                    CityModel model = new CityModel(key, citiesResult);
                    cityDao.insertAll(model);
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

}