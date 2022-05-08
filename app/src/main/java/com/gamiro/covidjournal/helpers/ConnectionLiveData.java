package com.gamiro.covidjournal.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import androidx.lifecycle.LiveData;

public class ConnectionLiveData extends LiveData<Boolean> {
    private Context context;

    public ConnectionLiveData(Context context) {
        this.context = context;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    @Override
    protected void onActive() {
        super.onActive();
        context.registerReceiver(
                networkReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        try {
            context.unregisterReceiver(networkReceiver);
        } catch (Exception ignored) {

        }
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            postValue(isConnected());
        }
    };
}

//class ConnectionLiveData(private val context: Context) : LiveData<Boolean>() {
//private val networkReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//        postValue(context.isConnected)
//        }
//        }
//
//        override fun onActive() {
//        super.onActive()
//        context.registerReceiver(
//        networkReceiver,
//        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        )
//        }
//
//        override fun onInactive() {
//        super.onInactive()
//        try {
//        context.unregisterReceiver(networkReceiver)
//        } catch (e: Exception) {
//        }
//        }
//        }val Context.isConnected: Boolean
//        get() =