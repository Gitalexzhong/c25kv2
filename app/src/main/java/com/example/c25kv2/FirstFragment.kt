package com.example.c25kv2

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.c25kv2.databinding.FragmentFirstBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The "Run" page with a vertical list of Weeks and Days.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    // Change this value to adjust the number of weeks
    private val totalWeeks = 9

    // Coherent Color Scheme
    private val colorWarmUpCoolDown = Color.parseColor("#90A4AE") // Blue Grey
    private val colorRun = Color.parseColor("#FF7043")           // Deep Orange
    private val colorRest = Color.parseColor("#4FC3F7")          // Light Blue

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
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
        val today = LocalDate.now()
        
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        var currentScheduledDate = today

        // "Get started" section
        val isTodayStart = currentScheduledDate.isEqual(today)
        val startTestId = "start_test"
        listItems.add(RunListItem.WeekHeader("Get started"))
        listItems.add(RunListItem.DayItem(
            id = startTestId,
            title = "First Test",
            target = if (isTodayStart) "Today" else "Target: ${currentScheduledDate.format(dateFormatter)}",
            emoji = if (sharedPref.getBoolean(startTestId, false)) "✅" else "🎯",
            description = "Let's see where you are! Run as far as you can in 10 minutes.",
            isCompleted = sharedPref.getBoolean(startTestId, false),
            segments = listOf(
                WorkoutSegment("Test Run", 10, colorRun)
            ),
            isExpanded = isTodayStart
        ))

        // Weekly sections
        for (w in 1..totalWeeks) {
            listItems.add(RunListItem.WeekHeader("Week $w"))
            for (d in 1..3) {
                // Increment schedule by 2 days for each subsequent workout
                currentScheduledDate = currentScheduledDate.plusDays(2)
                
                val isToday = currentScheduledDate.isEqual(today)
                val workoutId = "week_${w}_day_${d}"
                val isCompleted = sharedPref.getBoolean(workoutId, false)
                
                val segments = mutableListOf<WorkoutSegment>()
                segments.add(WorkoutSegment("Warm up", 5, colorWarmUpCoolDown))
                repeat(3) {
                    segments.add(WorkoutSegment("Run", 1 + (w/3), colorRun))
                    segments.add(WorkoutSegment("Rest", 2, colorRest))
                }
                segments.add(WorkoutSegment("Cool down", 5, colorWarmUpCoolDown))

                listItems.add(RunListItem.DayItem(
                    id = workoutId,
                    title = "Day $d",
                    target = if (isToday) "Today" else "Target: ${currentScheduledDate.format(dateFormatter)}",
                    emoji = if (isCompleted) "✅" else "🏃",
                    description = "A mix of walking and running to build your stamina slowly but surely.",
                    isCompleted = isCompleted,
                    segments = segments,
                    isExpanded = isToday
                ))
            }
        }

        binding.recyclerviewRun.adapter = RunListAdapter(listItems) { id, title ->
            val bundle = Bundle().apply {
                putString("workoutId", id)
                putString("workoutTitle", title)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_WorkoutFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class WorkoutSegment(val name: String, val durationMinutes: Int, val color: Int)

    sealed class RunListItem {
        data class WeekHeader(val title: String) : RunListItem()
        data class DayItem(
            val id: String,
            val title: String,
            val target: String,
            val emoji: String,
            val description: String,
            val isCompleted: Boolean,
            val segments: List<WorkoutSegment>,
            var isExpanded: Boolean = false
        ) : RunListItem()
    }

    private class RunListAdapter(
        private val items: List<RunListItem>,
        private val onStartClick: (String, String) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                DayViewHolder(view, onStartClick)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val item = items[position]) {
                is RunListItem.WeekHeader -> (holder as HeaderViewHolder).bind(item)
                is RunListItem.DayItem -> (holder as DayViewHolder).bind(item) {
                    item.isExpanded = !item.isExpanded
                    notifyItemChanged(position)
                }
            }
        }

        override fun getItemCount(): Int = items.size

        class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val textView: TextView = view.findViewById(R.id.text_week_header)
            fun bind(item: RunListItem.WeekHeader) {
                textView.text = item.title
            }
        }

        class DayViewHolder(view: View, private val onStartClick: (String, String) -> Unit) : RecyclerView.ViewHolder(view) {
            private val emoji: TextView = view.findViewById(R.id.text_emoji)
            private val title: TextView = view.findViewById(R.id.text_day_title)
            private val target: TextView = view.findViewById(R.id.text_target)
            private val duration: TextView = view.findViewById(R.id.text_duration)
            private val barContainer: LinearLayout = view.findViewById(R.id.layout_segmented_bar)
            private val collapsible: View = view.findViewById(R.id.layout_collapsible)
            private val segmentsDetails: TextView = view.findViewById(R.id.text_segments_details)
            private val description: TextView = view.findViewById(R.id.text_description)
            private val btnStart: Button = view.findViewById(R.id.btn_start)

            fun bind(item: RunListItem.DayItem, onClick: () -> Unit) {
                emoji.text = item.emoji
                title.text = item.title
                target.text = item.target
                description.text = item.description

                val totalMins = item.segments.sumOf { it.durationMinutes }
                duration.text = "Overall Time: $totalMins mins"

                barContainer.removeAllViews()
                item.segments.forEach { segment ->
                    val frame = FrameLayout(itemView.context)
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, segment.durationMinutes.toFloat())
                    frame.layoutParams = params
                    frame.setBackgroundColor(segment.color)

                    val text = TextView(itemView.context)
                    text.text = "${segment.durationMinutes}"
                    text.setTextColor(Color.WHITE)
                    text.textSize = 10f
                    text.gravity = Gravity.CENTER
                    val textParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    text.layoutParams = textParams
                    
                    frame.addView(text)
                    barContainer.addView(frame)
                }

                val details = item.segments.joinToString(", ") { "${it.durationMinutes}m ${it.name}" }
                segmentsDetails.text = details

                collapsible.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
                itemView.setOnClickListener { onClick() }

                btnStart.text = if (item.isCompleted) "RESTART" else "START"
                btnStart.setOnClickListener {
                    onStartClick(item.id, item.title)
                }
            }
        }
    }
}