// app/src/main/java/com/anglersparadise/ui/lake/LakeViewModel.java
package com.anglersparadise.ui.lake;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.anglersparadise.data.FishRepository;
import com.anglersparadise.domain.model.Fish;
import com.anglersparadise.domain.model.SpeciesCatalog;
import com.anglersparadise.util.SingleLiveEvent;
import java.util.Random;

public class LakeViewModel extends ViewModel {

    private final MutableLiveData<LakeState> _state = new MutableLiveData<>(LakeState.IDLE);
    public LiveData<LakeState> state = _state;

    private final SingleLiveEvent<String> _toast = new SingleLiveEvent<>();
    public LiveData<String> toast = _toast;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable waitTask = null;
    private final Random rng = new Random();

    public void cast() {
        if (_state.getValue() != LakeState.IDLE) return;
        _state.setValue(LakeState.WAITING);

        cancelWait();
        long delayMs = 1500 + rng.nextInt(2501); // 1500..4000
        waitTask = () -> {
            if (_state.getValue() == LakeState.WAITING) {
                _state.setValue(LakeState.HOOKED);
                _toast.setValue("Fish on!  Reel now!");
            }
        };
        handler.postDelayed(waitTask, delayMs);
    }

    public void reel() {
        LakeState s = _state.getValue();
        if (s == LakeState.HOOKED) {
            _state.setValue(LakeState.CAUGHT);
            _toast.setValue("You caught a fish.");
        } else if (s == LakeState.WAITING) {
            _state.setValue(LakeState.ESCAPED);
            _toast.setValue("Too early—fish got away.");
            handler.postDelayed(this::resetToIdle, 1200);
        }
    }

    public void confirmCatchToTank() {
        if (_state.getValue() == LakeState.CAUGHT) {
            String species = SpeciesCatalog.randomName();
            int size = SpeciesCatalog.randomSize();
            FishRepository.addToTank(FishRepository.newFish(species, size));
            _toast.setValue("Added a " + species + " (size " + size + ") to tank.");
            resetToIdle();
        }
    }

    public void letGo() {
        if (_state.getValue() == LakeState.CAUGHT) resetToIdle();
    }

    private void resetToIdle() {
        cancelWait();
        _state.setValue(LakeState.IDLE);
    }

    private void cancelWait() {
        if (waitTask != null) {
            handler.removeCallbacks(waitTask);
            waitTask = null;
        }
    }

    @Override protected void onCleared() {
        super.onCleared();
        cancelWait();
    }
}
