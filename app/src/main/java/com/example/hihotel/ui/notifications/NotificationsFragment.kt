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

        if (userStatus == "admin") {
            binding.profileName.text = "Администратор:Имангазиев Ислам"  // Имя администратора
            binding.profilePhone.text = "Телефон: +996500011100"
            binding.profileRole.text = "Роль: Администратор"
        } else {
            binding.profileName.text = "Пользователь"
            binding.profilePhone.text = "Телефон: +99779445939"
            binding.profileRole.text = "Роль: Пользователь"
        }
    }
    private fun getUserStatus(): String? {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_status", "user")
    }

}