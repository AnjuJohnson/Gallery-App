package com.zinedroid.gallerynew.base;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;


import com.zinedroid.gallerynew.common.Constants;

import java.net.URLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Cecil Paul on 31/8/17.
 */

public class BaseFragment extends Fragment {
    public BaseFragment() {
        super();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public static boolean isImageFile(String path) {
       /* String mimeType = URLConnection.guessContentTypeFromName(path);

        return mimeType != null && mimeType.startsWith("image");*/
        if((path.endsWith(".jpeg"))||(path.endsWith(".png"))||(path.endsWith(".gif"))){
            return true;
        }
        else return false;
    }

    public static boolean isNoMediaFile(String path) {
      //  String mimeType = URLConnection.guessContentTypeFromName(path);
        if(path.endsWith(".nomedia")){
            return true;
        }
        else return false;
    }



    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    public void setSharedPreference(String key, String value) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedPreference(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
        return prefs.getString(key, "DEFAULT");
    }
}
