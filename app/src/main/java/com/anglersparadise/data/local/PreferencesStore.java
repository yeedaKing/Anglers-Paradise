// app/src/main/java/com/anglersparadise/data/local/PreferencesStore.java

package com.anglersparadise.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.anglersparadise.domain.model.Fish;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class PreferencesStore {
    private static Context appContext;
    private static final String PREFS = "tank_store_prefs";
    private static final String KEY_TANK = "tank_json";
    private static final String KEY_HISTORY = "history_json";
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Fish>>(){}.getType();

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private static SharedPreferences prefs() {
        return appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static List<Fish> loadTank() {
        String json = prefs().getString(KEY_TANK, null);
        if (json == null) return Collections.emptyList();
        try { return GSON.fromJson(json, LIST_TYPE); } catch (Exception e) { return Collections.emptyList(); }
    }

    public static List<Fish> loadHistory() {
        String json = prefs().getString(KEY_HISTORY, null);
        if (json == null) return Collections.emptyList();
        try { return GSON.fromJson(json, LIST_TYPE); } catch (Exception e) { return Collections.emptyList(); }
    }

    public static void saveTank(List<Fish> list) {
        prefs().edit().putString(KEY_TANK, GSON.toJson(list, LIST_TYPE)).apply();
    }

    public static void saveHistory(List<Fish> list) {
        prefs().edit().putString(KEY_HISTORY, GSON.toJson(list, LIST_TYPE)).apply();
    }

    private PreferencesStore() {}
}
