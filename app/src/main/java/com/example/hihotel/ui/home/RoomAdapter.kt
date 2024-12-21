package com.example.hihotel.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hihotel.databinding.ItemRoomBinding
import com.example.hihotel.ui.Room

class RoomAdapter(private val roomList: List<Room>, private val onDelete: (String) -> Unit,private val onSaveRoom: (Room) -> Unit,private val userStatus: String) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)

        holder.itemView.setOnLongClickListener {
            onDelete(room.id)
            true
        }
        if (userStatus == "admin") {
            holder.binding.bookButton.visibility = View.GONE
        } else {
            holder.binding.bookButton.visibility = View.VISIBLE
            holder.binding.bookButton.setOnClickListener {
                onSaveRoom(room)
            }
        }

    }

    override fun getItemCount() = roomList.size

    inner class RoomViewHolder(val binding: ItemRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            binding.roomTitle.text = room.name
            binding.roomPrice.text = "Price: ${room.price} $"
            binding.roomDescription.text = room.description
            Glide.with(binding.root).load(room.imageUrl).into(binding.roomImage)
        }
    }
}
