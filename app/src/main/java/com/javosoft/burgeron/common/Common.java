package com.javosoft.burgeron.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.javosoft.burgeron.model.UserModel;

public class Common {
    public static final String USER_REFERENCES = "Users";
    public static UserModel currentUser;

    public static String restaurantSelected = "";
    public static String categorySelected = "";

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
