// app/src/main/java/com/anglersparadise/ui/tank/FishBasketDialog.kt
package com.anglersparadise.ui.tank

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anglersparadise.data.FishRepository
import com.anglersparadise.databinding.DialogFishBasketBinding
import com.anglersparadise.domain.model.Fish
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle

class FishBasketDialog : DialogFragment() {

    private var _binding: DialogFishBasketBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FishAdapter
    private val vm: TankViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFishBasketBinding.inflate(layoutInflater)

        adapter = FishAdapter { fish -> onFishClicked(fish) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        binding.btnClose.setOnClickListener { dismiss() }

        return AlertDialog.Builder(requireContext())
            .setTitle("Fish Basket")
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()

        // Collect on the fragment lifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                FishRepository.history.collect { list ->
                    adapter.submitList(list.toList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val w = (resources.displayMetrics.widthPixels * 0.95f).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.75f).toInt()
        dialog?.window?.setLayout(w, h)
    }

    private fun onFishClicked(f: Fish) {
        AlertDialog.Builder(requireContext())
            .setTitle(f.species)
            .setMessage("ID: ${f.id}\nSize: ${f.size}")
            .setPositiveButton("Release from Tank") { _, _ -> vm.releaseFish(f.id) }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

