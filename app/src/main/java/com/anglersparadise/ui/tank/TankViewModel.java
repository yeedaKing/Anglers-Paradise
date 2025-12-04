// app/src/main/java/com/anglersparadise/ui/tank/TankViewModel.java

package com.anglersparadise.ui.tank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.anglersparadise.data.FishRepository;
import com.anglersparadise.domain.model.Fish;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple polling ViewModel: we expose LiveData snapshots by polling the repo.
 */
public class TankViewModel extends ViewModel {
    private final MutableLiveData<List<Fish>> _tankFish = new MutableLiveData<>(FishRepository.getTankSnapshot());
    public LiveData<List<Fish>> tankFish = _tankFish;

    private Timer timer;

    public TankViewModel() {
        // Poll every 500ms to reflect changes (cheap lists).
        timer = new Timer("tank-poll", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                // Post from background
                _tankFish.postValue(FishRepository.getTankSnapshot());
            }
        }, 0, 500);
    }

    public void releaseFish(long id) { FishRepository.removeFromTank(id); }
    public void releaseAll() { FishRepository.clearTank(); }

    @Override protected void onCleared() {
        super.onCleared();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
