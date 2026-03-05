package com.example.c25kv2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.c25kv2.databinding.FragmentSecondBinding

/**
 * Settings page with a Debug Mode toggle and Reset option.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.switchDebugMode.isChecked = sharedPref.getBoolean("debug_mode", false)

        binding.switchDebugMode.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("debug_mode", isChecked)
                apply()
            }
        }

        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Reset Progress")
                .setMessage("Are you sure you want to reset all progress? This cannot be undone.")
                .setPositiveButton("Reset") { _, _ ->
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    binding.switchDebugMode.isChecked = false
                    Toast.makeText(requireContext(), "All progress has been reset.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}