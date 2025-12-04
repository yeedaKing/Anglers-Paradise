// app/src/main/java/com/anglersparadise/ui/tank/FishBasketDialog.java
package com.anglersparadise.ui.tank;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anglersparadise.data.FishRepository;
import com.anglersparadise.databinding.DialogFishBasketBinding;
import com.anglersparadise.domain.model.Fish;

import java.util.List;

public class FishBasketDialog extends DialogFragment {

    private DialogFishBasketBinding binding;
    private FishAdapter adapter;
    private TankViewModel vm;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable poll = new Runnable() {
        @Override public void run() {
            List<Fish> snapshot = FishRepository.getHistorySnapshot();
            // submit a new instance to trigger DiffUtil
            adapter.submitList(snapshot);
            handler.postDelayed(this, 600);
        }
    };

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogFishBasketBinding.inflate(getLayoutInflater());

        vm = new ViewModelProvider(requireActivity()).get(TankViewModel.class);

        adapter = new FishAdapter(f -> onFishClicked(f));
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnClearHistory.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Clear basket history?")
                        .setMessage("This removes the ever-caught list. Fish currently in the tank remain.")
                        .setPositiveButton("Yes", (d, w) -> FishRepository.clearHistory())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        return new AlertDialog.Builder(requireContext())
                .setTitle("Fish Basket")
                .setView(binding.getRoot())
                .create();
    }

    @Override public void onStart() {
        super.onStart();
        handler.post(poll);
    }

    @Override public void onResume() {
        super.onResume();
        int w = (int) (getResources().getDisplayMetrics().widthPixels * 0.95f);
        int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75f);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(w, h);
        }
    }

    @Override public void onStop() {
        super.onStop();
        handler.removeCallbacks(poll);
    }

    private void onFishClicked(Fish f) {
        new AlertDialog.Builder(requireContext())
                .setTitle(f.getSpecies())
                .setMessage("ID: " + f.getId() + "\nSize: " + f.getSize())
                .setPositiveButton("Release from Tank", (d, w) -> vm.releaseFish(f.getId()))
                .setNegativeButton("Close", null)
                .show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
