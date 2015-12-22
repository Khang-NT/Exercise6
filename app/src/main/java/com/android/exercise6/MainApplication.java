package com.android.exercise6;

import android.app.Application;

import com.android.exercise6.util.SqliteHelper;
import com.android.exercise6.util.VolleySingleton;

/**
 * Created by Khang on 07/12/2015.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SqliteHelper.initialize(this);
        VolleySingleton.initialize(this);
    }

}
