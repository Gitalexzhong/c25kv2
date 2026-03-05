package com.example.c25kv2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.c25kv2.databinding.FragmentFirstBinding

/**
 * The "Run" page with a vertical list of Weeks and Days.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    // Change this value to adjust the number of weeks
    private val totalWeeks = 9

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listItems = mutableListOf<RunListItem>()
        
        // "Get started" section
        listItems.add(RunListItem.WeekHeader("Get started"))
        listItems.add(RunListItem.DayItem(
            title = "First Test",
            target = "Target: Dec 1, 2023",
            emoji = "🎯",
            description = "Let's see where you are! Run as far as you can in 10 minutes.",
            duration = "10 mins",
            isCompleted = false
        ))

        // Weekly sections
        for (w in 1..totalWeeks) {
            listItems.add(RunListItem.WeekHeader("Week $w"))
            for (d in 1..3) {
                listItems.add(RunListItem.DayItem(
                    title = "Day $d",
                    target = if (w == 1 && d == 1) "Completed 2 days ago" else "Target: Dec ${w*7 + d}, 2023",
                    emoji = if (w == 1 && d == 1) "✅" else "🏃",
                    description = "Alternating 60s of jogging and 90s of walking for a total of 20 minutes.",
                    duration = "20 mins",
                    isCompleted = (w == 1 && d == 1)
                ))
            }
        }

        binding.recyclerviewRun.adapter = RunListAdapter(listItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    sealed class RunListItem {
        data class WeekHeader(val title: String) : RunListItem()
        data class DayItem(
            val title: String,
            val target: String,
            val emoji: String,
            val description: String,
            val duration: String,
            val isCompleted: Boolean
        ) : RunListItem()
    }

    private class RunListAdapter(private val items: List<RunListItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_HEADER = 0
            private const val TYPE_DAY = 1
        }

        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is RunListItem.WeekHeader -> TYPE_HEADER
                is RunListItem.DayItem -> TYPE_DAY
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_HEADER) {
                val view = inflater.inflate(R.layout.item_week_header, parent, false)
                HeaderViewHolder(view)
            } else {
                val view = inflater.inflate(R.layout.item_day, parent, false)
                DayViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val item = items[position]) {
                is RunListItem.WeekHeader -> (holder as HeaderViewHolder).bind(item)
                is RunListItem.DayItem -> (holder as DayViewHolder).bind(item)
            }
        }

        override fun getItemCount(): Int = items.size

        class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val textView: TextView = view.findViewById(R.id.text_week_header)
            fun bind(item: RunListItem.WeekHeader) {
                textView.text = item.title
            }
        }

        class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val emoji: TextView = view.findViewById(R.id.text_emoji)
            private val title: TextView = view.findViewById(R.id.text_day_title)
            private val target: TextView = view.findViewById(R.id.text_target)
            private val description: TextView = view.findViewById(R.id.text_description)
            private val duration: TextView = view.findViewById(R.id.text_duration)
            private val btnStart: Button = view.findViewById(R.id.btn_start)

            fun bind(item: RunListItem.DayItem) {
                emoji.text = item.emoji
                title.text = item.title
                target.text = item.target
                description.text = item.description
                duration.text = "Duration: ${item.duration}"
                
                btnStart.text = if (item.isCompleted) "RESTART" else "START"
                btnStart.setOnClickListener {
                    Toast.makeText(it.context, "Starting ${item.title}...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}