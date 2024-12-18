package com.example.hihotel.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hihotel.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private val binding: FragmentDashboardBinding by lazy {
        FragmentDashboardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayBookedRoom()
    }

    private fun displayBookedRoom() {
        val sharedPreferences =
            requireContext().getSharedPreferences("booking_pref", Context.MODE_PRIVATE)

        val roomName = sharedPreferences.getString("booked_room_name", "Нет брони")
        val roomPrice = sharedPreferences.getString("booked_room_price", "")
        val roomDate=sharedPreferences.getString("room_date","")

        if (roomName == "Нет брони") {
            binding.bTitle.text = "Вы пока ничего не забронировали."
            binding.bPrice.text = ""
            binding.bDate.text = ""
        } else {
            binding.bTitle.text = "Вы забронировали комнату:\n\nНазвание: $roomName"
            binding.bPrice.text = "Цена: $roomPrice"
            binding.bDate.text = "Дата: $roomDate"
        }
    }
}