// app/src/main/java/com/anglersparadise/data/FishRepository.java

package com.anglersparadise.data;

import com.anglersparadise.data.local.PreferencesStore;
import com.anglersparadise.domain.model.Fish;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class FishRepository {

    private static final List<Fish> tank = new ArrayList<>();
    private static final List<Fish> history = new ArrayList<>();
    private static final AtomicLong nextId = new AtomicLong(1L);

    /** Call once at app start (Application.onCreate). */
    public static void initFromDisk() {
        synchronized (FishRepository.class) {
            tank.clear();
            tank.addAll(PreferencesStore.loadTank());
            history.clear();
            history.addAll(PreferencesStore.loadHistory());
            long max = 0L;
            for (Fish f : tank) if (f.getId() > max) max = f.getId();
            for (Fish f : history) if (f.getId() > max) max = f.getId();
            nextId.set(max + 1);
        }
    }

    public static Fish newFish(String species, int size) {
        return new Fish(nextId.getAndIncrement(), species, size, System.currentTimeMillis());
    }

    public static synchronized void addToTank(Fish fish) {
        tank.add(fish);
        history.add(fish);
        PreferencesStore.saveTank(tank);
        PreferencesStore.saveHistory(history);
    }

    public static synchronized void removeFromTank(long id) {
        tank.removeIf(f -> f.getId() == id);
        PreferencesStore.saveTank(tank);
        // history is ever-caught; do not remove
    }

    public static synchronized void clearTank() {
        tank.clear();
        PreferencesStore.saveTank(tank);
    }

    public static synchronized void clearHistory() {
        history.clear();
        PreferencesStore.saveHistory(history);
    }

    public static synchronized List<Fish> getTankSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(tank));
    }

    public static synchronized List<Fish> getHistorySnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    private FishRepository() {}
}
