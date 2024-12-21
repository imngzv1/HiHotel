package com.example.hihotel.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hihotel.databinding.ItemBookingBinding

class BookingsAdapter (private val bookings:List<Booking>): RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>(){
    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context),parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.binding.tvRoomId.text = booking.roomId
        holder.binding.tvDate.text = booking.date
    }

    override fun getItemCount(): Int = bookings.size
}