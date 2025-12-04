// app/src/main/java/com/anglersparadise/ui/tank/FishAdapter.java
package com.anglersparadise.ui.tank;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.anglersparadise.databinding.ItemFishBinding;
import com.anglersparadise.domain.model.Fish;

public class FishAdapter extends ListAdapter<Fish, FishAdapter.VH> {

    public interface OnClick { void onClick(Fish fish); }

    private final OnClick onClick;

    public FishAdapter(OnClick onClick) {
        super(DIFF);
        this.onClick = onClick;
    }

    public static final DiffUtil.ItemCallback<Fish> DIFF = new DiffUtil.ItemCallback<Fish>() {
        @Override public boolean areItemsTheSame(@NonNull Fish oldItem, @NonNull Fish newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override public boolean areContentsTheSame(@NonNull Fish oldItem, @NonNull Fish newItem) {
            // Since id is unique and immutable, and we show species/size, simple equality on id is fine
            return oldItem.getId() == newItem.getId()
                    && oldItem.getSize() == newItem.getSize()
                    && safeEq(oldItem.getSpecies(), newItem.getSpecies());
        }
        private boolean safeEq(String a, String b) { return (a == null) ? (b == null) : a.equals(b); }
    };

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        ItemFishBinding b = ItemFishBinding.inflate(inf, parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    class VH extends RecyclerView.ViewHolder {
        private final ItemFishBinding b;
        VH(ItemFishBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
        void bind(Fish f) {
            b.title.setText(f.getSpecies());
            b.subtitle.setText("ID " + f.getId() + " • size " + f.getSize());
            b.getRoot().setOnClickListener(v -> onClick.onClick(f));
        }
    }
}
