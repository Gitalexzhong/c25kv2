package com.example.c25kv2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.c25kv2.databinding.FragmentWorkoutBinding
import java.util.Locale

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val CHANNEL_ID = "workout_channel"
    private val NOTIFICATION_ID = 1

    private val timerRunnable = object : Runnable {
        override fun run() {
            seconds++
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            val timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
            binding.textTimer.text = timeString
            updateNotification(timeString)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutTitle = arguments?.getString("workoutTitle") ?: "Workout"
        binding.textWorkoutInfo.text = workoutTitle

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        createNotificationChannel()
        startWorkout()
    }

    private fun startWorkout() {
        handler.post(timerRunnable)
        showNotification("Workout Started", "00:00:00")
    }

    private fun stopWorkout() {
        handler.removeCallbacks(timerRunnable)
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Workout Tracking"
            val descriptionText = "Shows active workout progress"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun updateNotification(content: String) {
        val workoutTitle = arguments?.getString("workoutTitle") ?: "Workout"
        showNotification(workoutTitle, "Duration: $content")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopWorkout()
        _binding = null
    }
}