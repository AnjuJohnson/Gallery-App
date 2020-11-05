package com.zinedroid.gallerynew.base;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by Cecil Paul on 15/11/17.
 */

public class BaseService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

//    public void setSharedPreference(String key, String value) {
//        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
//
//    public String getSharedPreference(String key) {
//        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_KEY, MODE_PRIVATE);
//        return prefs.getString(key, "DEFAULT");
//    }

}
