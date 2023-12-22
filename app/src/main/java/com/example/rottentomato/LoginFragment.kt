package com.example.rottentomato

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rottentomato.databinding.LoginFragmentBinding
import com.example.rottentomato.databinding.RegisterFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

class LoginFragment : Fragment() {
    private lateinit var binding : LoginFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASS = "extra_pass"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPreferences = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("id", firebaseAuth.currentUser?.uid)
        editor.putString("email", firebaseAuth.currentUser?.email)
        editor.putString("password", binding.password.text.toString().trim())
        editor.apply()

        if (sharedPreferences.getBoolean("isLogin", false)) {
            isUserAdmin(sharedPreferences.getString("email", "").toString())
        }

        with(binding) {
            loginBtn.setOnClickListener {
                val email = email.text.toString().trim()
                val password = password.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isUserAdmin(email)
                            } else {
                                Toast.makeText(requireContext(), "Username or Password is incorrect", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun isUserAdmin(email: String) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            if (user.email == "admin@gmail.com") {
                startActivity(Intent(requireContext(), AdminActivity::class.java).apply {
                    putExtra(EXTRA_EMAIL, email)
                    putExtra(EXTRA_PASS, binding.password.text.toString().trim())
                })
            } else {
                startActivity(Intent(requireContext(), HomeScreenActivity::class.java).apply {
                    putExtra(EXTRA_EMAIL, email)
                    putExtra(EXTRA_PASS, binding.password.text.toString().trim())
                })
            }
        }
    }
}
