package com.example.hihotel.ui.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.hihotel.MainActivity
import com.example.hihotel.R
import com.example.hihotel.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            (activity as MainActivity).hideBottomNav()
        }
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user?.email == "admin@gmail.com") {
                                saveUserStatus("admin")
                                findNavController().navigate(R.id.navigation_home)
                                (activity as MainActivity).showBottomNav()
                                (activity as MainActivity).updateAdminMenuVisibility()
                            } else {
                                saveUserStatus("user")
                                findNavController().navigate(R.id.navigation_home)
                                (activity as MainActivity).showBottomNav()
                                (activity as MainActivity).updateAdminMenuVisibility()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Неправильный логин или пароль",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

            } else {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnAuth.setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
    }

    private fun saveUserStatus(status: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("user_status", status).apply()
    }
}