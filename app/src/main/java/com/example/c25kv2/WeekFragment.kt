package com.example.c25kv2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.c25kv2.databinding.FragmentWeekBinding
import com.google.android.material.tabs.TabLayoutMediator

class WeekFragment : Fragment() {
    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekNumber = arguments?.getInt(ARG_WEEK_NUMBER) ?: 1

        binding.viewPagerDays.adapter = DaysAdapter(this)
        TabLayoutMediator(binding.tabLayoutDays, binding.viewPagerDays) { tab, position ->
            tab.text = "Day ${position + 1}"
        }.attach()
    }

    private class DaysAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return DayFragment.newInstance(position + 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WEEK_NUMBER = "week_number"
        fun newInstance(weekNumber: Int) = WeekFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_WEEK_NUMBER, weekNumber)
            }
        }
    }
}