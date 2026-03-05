package com.example.c25kv2

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.c25kv2.databinding.FragmentStatsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Stats page with real progress tracking.
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private var progressAnimator: ValueAnimator? = null

    private val totalWeeks = 9
    private val sessionsPerWeek = 3
    private val totalSessions = 1 + (totalWeeks * sessionsPerWeek) // +1 for First Test

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStats()
    }

    private fun updateStats() {
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Count completed sessions
        var completedCount = 0
        if (sharedPref.getBoolean("start_test", false)) completedCount++
        
        for (w in 1..totalWeeks) {
            for (d in 1..3) {
                if (sharedPref.getBoolean("week_${w}_day_${d}", false)) {
                    completedCount++
                }
            }
        }

        // Calculate Percentage
        val percentage = if (totalSessions > 0) (completedCount * 100) / totalSessions else 0
        animateProgress(percentage)

        // Session Count Text
        binding.textSessionCount.text = "$completedCount / $totalSessions sessions completed"

        // Weeks Remaining Logic
        val remainingSessions = totalSessions - completedCount
        val remainingWeeks = (remainingSessions + sessionsPerWeek - 1) / sessionsPerWeek 
        binding.textWeeksToGoal.text = "$remainingWeeks weeks until goal"

        // Goal Date (2 days per session estimate)
        val today = LocalDate.now()
        val targetDate = today.plusDays((remainingSessions * 2).toLong())
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
        binding.textGoalDate.text = "Target Date: ${targetDate.format(dateFormatter)}"
    }

    private fun animateProgress(target: Int) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofInt(0, target).apply {
            duration = 1000
            addUpdateListener { animation ->
                _binding?.let { b ->
                    val value = animation.animatedValue as Int
                    b.progressBarGoal.progress = value
                    b.textPercentage.text = "$value%"
                }
            }
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimator?.cancel()
        _binding = null
    }
}