package com.zinedroid.gallerynew.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Cecil Paul on 31/8/17.
 */

public class BaseActivity extends AppCompatActivity {

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

//    public void setSharedPreference(String key, String value) {
//        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
//    public void clearSharedPreference() {
//        SharedPreferences.Editor pref = this.getSharedPreferences(Constants.SHARED_KEY, 0).edit();
//        pref.clear();
//        pref.commit();
//    }
//    public String getSharedPreference(String key) {
//        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
//        return prefs.getString(key, "DEFAULT");
//    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        BaseApplication.activityResumed();// On Resume notify the Application
    }

}
