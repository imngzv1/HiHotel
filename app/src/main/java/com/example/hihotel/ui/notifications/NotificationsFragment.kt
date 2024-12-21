package com.example.hihotel.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hihotel.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth

class NotificationsFragment : Fragment() {

private val binding: FragmentNotificationsBinding by lazy {
    FragmentNotificationsBinding.inflate(layoutInflater)
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

        val userStatus = getUserStatus()  // "admin" или "user"
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: "Неизвестный пользователь"

        binding.etUsername.text = "Ваша почта: $email"

        if (userStatus == "admin") {
            binding.profileName.text = "Администратор:Имангазиев Ислам"  // Имя администратора
            binding.profilePhone.text = "Телефон: +996500011100"
        } else {
            binding.profileName.text = "Клиент"
            binding.profilePhone.text = "Телефон: +99779445939"
        }
    }

    private fun getUserStatus(): String? {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_status", "user")
    }

}