package com.example.hihotel.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hihotel.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {
    private val binding: FragmentDashboardBinding by lazy {
        FragmentDashboardBinding.inflate(layoutInflater)
    }
    private val existingBookings = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewBookings.layoutManager = LinearLayoutManager(requireContext())
        fetchBookings()
    }

    private fun fetchBookings() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    existingBookings.clear()
                    val bookings = documents.map { document ->
                        val roomId = document.getString("roomName") ?: ""
                        val date = document.getString("date") ?: ""
                        Booking(roomId, date)
                    }
                    existingBookings.addAll(bookings)
                    updateDashboard(existingBookings)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateDashboard(bookings: List<Booking>) {
        val adapter = BookingsAdapter(bookings)
        binding.recyclerViewBookings.adapter = adapter
    }
}