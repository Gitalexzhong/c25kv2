package com.example.c25kv2

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.c25kv2.databinding.FragmentStatsBinding

/**
 * Stats page with animated progress.
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animate progress bar and percentage text
        val targetProgress = 35 // Example: 35% complete
        animateProgress(targetProgress)
    }

    private fun animateProgress(target: Int) {
        val animator = ValueAnimator.ofInt(0, target)
        animator.duration = 1500 // 1.5 seconds animation
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            binding.progressBarGoal.progress = value
            binding.textPercentage.text = "$value%"
        }
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}