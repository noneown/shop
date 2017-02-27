package com.nshop.nshop;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 */
public class NJLApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }
}
