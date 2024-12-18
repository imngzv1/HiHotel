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


class LoginFragment : Fragment() {

    private val binding:FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post{
            (activity as MainActivity).hideBottomNav()
        }

        binding.btnLogin.setOnClickListener {
            val username=binding.etUsername.text.toString()
            val password=binding.etPassword.text.toString()
            if(username=="admin" && password=="123"){
                saveUserStatus("admin")
                findNavController().navigate(R.id.navigation_home)
                (activity as MainActivity).showBottomNav()
                (activity as MainActivity).updateAdminMenuVisibility()
            }else if(username=="user" && password=="123"){
                saveUserStatus("user")
                findNavController().navigate(R.id.navigation_home)
                (activity as MainActivity).showBottomNav()
                (activity as MainActivity).updateAdminMenuVisibility()
            }else{
                Toast.makeText(requireContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveUserStatus(status: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("user_status", status).apply()
    }

}