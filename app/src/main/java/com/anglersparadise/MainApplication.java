// app/src/main/java/com/anglersparadise/MainApplication.java
package com.anglersparadise;

import android.app.Application;
import com.anglersparadise.data.FishRepository;
import com.anglersparadise.data.local.PreferencesStore;

public class MainApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        PreferencesStore.init(this);
        FishRepository.initFromDisk();
    }
}
