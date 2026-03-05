package com.example.c25kv2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.c25kv2.databinding.FragmentDayBinding

class DayFragment : Fragment() {
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dayNumber = arguments?.getInt(ARG_DAY_NUMBER) ?: 1
        binding.textDayContent.text = "Content for Day $dayNumber"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DAY_NUMBER = "day_number"
        fun newInstance(dayNumber: Int) = DayFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_DAY_NUMBER, dayNumber)
            }
        }
    }
}