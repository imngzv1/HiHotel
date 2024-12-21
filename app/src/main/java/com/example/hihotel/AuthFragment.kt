package com.example.hihotel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.hihotel.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth


class AuthFragment : Fragment() {
    private val binding: FragmentAuthBinding by lazy {
        FragmentAuthBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            val email = binding.etAuthEmail.text.toString()
            val password = binding.etAuthPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Регистрация прошла успешно",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.loginFragment)
                            (activity as MainActivity).showBottomNav()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Правильно заполните поля!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Введите почту и пароль", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

}




