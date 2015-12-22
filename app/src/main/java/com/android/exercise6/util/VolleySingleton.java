package com.android.exercise6.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Khang on 07/12/2015.
 */
public class VolleySingleton {
    private static RequestQueue mRequestQueue;

    public static void initialize(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public synchronized static RequestQueue getRequestQueue(){
        assert mRequestQueue != null : "Initialize First";
        return mRequestQueue;
    }
}
