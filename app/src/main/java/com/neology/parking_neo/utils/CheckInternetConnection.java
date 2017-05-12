package com.neology.parking_neo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Cesar Segura on 11/05/2017.
 */

public class CheckInternetConnection {
    public static boolean isConnectedToInternet(Context c) {
        ConnectivityManager connectivity = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

}
