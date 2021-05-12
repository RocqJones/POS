package com.intoverflown.pos.patterns;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mySingleton;
    private RequestQueue requestQueue;
    private static Context c;

    // object of class
    private MySingleton(Context context) {
        c = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(c.getApplicationContext());
        }
        return requestQueue;
    }

    // method that return instance of this class
    public static synchronized MySingleton getInstance(Context context) {
        if (mySingleton == null) {
            mySingleton = new MySingleton(context);
        }
        return mySingleton;
    }

    // method that accepts request
    public<T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
