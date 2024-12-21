package com.example.hihotel.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hihotel.databinding.FragmentHomeBinding
import com.example.hihotel.ui.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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

        val userStatus = getUserStatus()

        roomAdapter = RoomAdapter(roomList, { roomId ->
            if (userStatus == "admin") {
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
                            bookRoom(room,selectedDate)
                        }
                    }
                    .setNeutralButton("Отмена", null)
                    .show()
            }

        },userStatus.toString())

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
    private fun bookRoom(room: Room, date: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("bookings")
                .whereEqualTo("roomName", room.name)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val booking = hashMapOf(
                            "roomName" to room.name,
                            "userId" to userId,
                            "date" to date
                            )
                        FirebaseFirestore.getInstance().collection("bookings")
                            .add(booking)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Комната забронирована на $date",
                                    Toast.LENGTH_SHORT
                                ).show()
                                createExcelFile()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка бронирования: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Комната занята на $date, пожалуйста выберите другую дату.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
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
    private fun createExcelFile() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(requireContext(), "Нет бронирований для экспорта", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                try {
                    // Попытка найти существующий файл
                    val fileName = "Bookings.xlsx"
                    val file = requireContext().getExternalFilesDir(null)?.let {
                        File(it, fileName)
                    }

                    val workbook: XSSFWorkbook
                    val sheet: org.apache.poi.ss.usermodel.Sheet

                    // Если файл существует, открываем его, иначе создаем новый
                    if (file != null && file.exists()) {
                        val fileInputStream = FileInputStream(file)
                        workbook = WorkbookFactory.create(fileInputStream) as XSSFWorkbook
                        sheet = workbook.getSheetAt(0) ?: workbook.createSheet("Бронирования")
                    } else {
                        workbook = XSSFWorkbook()  // Новый файл
                        sheet = workbook.createSheet("Бронирования")
                        // Создаем заголовки
                        val headerRow = sheet.createRow(0)
                        headerRow.createCell(0).setCellValue("Имя комнаты")
                        headerRow.createCell(1).setCellValue("ID пользователя")
                        headerRow.createCell(2).setCellValue("Дата")
                    }

                    // Находим следующую строку для добавления данных
                    var rowIndex = sheet.lastRowNum + 1

                    // Заполняем данными
                    for (document in result) {
                        val row = sheet.createRow(rowIndex++)
                        row.createCell(0).setCellValue(document.getString("roomName") ?: "")
                        row.createCell(1).setCellValue(document.getString("userId") ?: "")
                        row.createCell(2).setCellValue(document.getString("date") ?: "")
                    }

                    // Сохраняем файл
                    val fileOut = FileOutputStream(file)
                    workbook.write(fileOut)
                    fileOut.close()
                    workbook.close()

                    Toast.makeText(requireContext(), "Экспортировано: ${file?.absolutePath}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка экспорта: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Ошибка при загрузке данных: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}