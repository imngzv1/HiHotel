package com.example.hihotel.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hihotel.databinding.FragmentHomeBinding
import com.example.hihotel.ui.Room
import com.example.hihotel.ui.dashboard.DashboardFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private lateinit var roomAdapter: RoomAdapter
    private val roomList = mutableListOf<Room>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userStatus = getUserStatus() // Получение статуса пользователя

        roomAdapter = RoomAdapter(roomList, { roomId ->
            if (userStatus == "admin") {
                // Диалог для удаления комнаты
                AlertDialog.Builder(requireContext())
                    .setTitle("Редактирование")
                    .setMessage("Вы хотите удалить комнату?")
                    .setPositiveButton("Удалить") { _, _ ->
                        deleteRoom(roomId) // Удаление комнаты
                        Toast.makeText(requireContext(), "Комната удалена", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .setNeutralButton("Отмена", null)
                    .show()
            }
        }, { room ->
            if (userStatus == "user") {
                AlertDialog.Builder(requireContext())
                    .setTitle("Бронирование")
                    .setMessage("Вы хотите забронировать комнату?")
                    .setPositiveButton("Забронировать") { _, _ ->
                        showDatePicker { selectedDate ->
                            saveRoomToPreferences(room, selectedDate)
                            Toast.makeText(
                                requireContext(),
                                "Комната забронирована на $selectedDate",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNeutralButton("Отмена", null)
                    .show()
            }

        })

        binding.rv.layoutManager = LinearLayoutManager(context)
        binding.rv.adapter = roomAdapter

        fetchRoomsFromFirestore() // Загрузка данных о комнатах
    }


    private fun getUserStatus(): String? {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_status", "user")
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate) // Передаём выбранную дату в callback
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    private fun saveRoomToPreferences(room: Room,date:String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("booking_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("booked_room_name", room.name)
        editor.putString("booked_room_price", room.price.toString())
        editor.putString("booked_room_description", room.description)
        editor.putString("room_id", room.id)
        editor.putString("room_date", date)
        editor.apply()
    }


    private fun fetchRoomsFromFirestore() {
        FirebaseFirestore.getInstance().collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                roomList.clear()
                for (document in result) {
                    val room = document.toObject(Room::class.java)
                    room.id = document.id // Присваиваем ID для удаления
                    roomList.add(room)
                }
                roomAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteRoom(roomId: String) {
        FirebaseFirestore.getInstance().collection("rooms")
            .document(roomId)
            .delete()
            .addOnSuccessListener {
                fetchRoomsFromFirestore() // Обновить список комнат после удаления
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Произошла ошибка при удалении: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}