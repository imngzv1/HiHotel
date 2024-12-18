package com.example.hihotel.ui.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hihotel.R
import com.example.hihotel.databinding.FragmentAdminBinding
import com.example.hihotel.ui.Room
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AdminFragment : Fragment() {

    private val binding: FragmentAdminBinding by lazy {
        FragmentAdminBinding.inflate(layoutInflater)
    }
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRoomButton.setOnClickListener {
            val rName = binding.roomName.text.toString()
            val rDescription = binding.roomDescription.text.toString()
            val rPrice = binding.roomPrice.text.toString().toDoubleOrNull() ?: 0.0

            addRoomToFirestore(rName, rDescription, rPrice)

        }
        binding.uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            if (imageUri == null) {
                Toast.makeText(requireContext(), "Не выбрано изображение", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addRoomToFirestore(name: String, description: String, price: Double) {
        if (imageUri != null) {
            val imageRef: StorageReference =
                storageRef.child("rooms/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val db = FirebaseFirestore.getInstance()
                            val room = hashMapOf(
                                "name" to name,
                                "description" to description,
                                "price" to price,
                                "imageUrl" to uri.toString()
                            )
                            db.collection("rooms")
                                .add(room)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Комната добавлена!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Ошибка: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки изображения",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}