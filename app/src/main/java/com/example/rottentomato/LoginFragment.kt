package com.example.rottentomato

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.rottentomato.databinding.LoginFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture


class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences =
            requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val emailLayout: TextInputLayout = binding.emailField
        val passwordLayout: TextInputLayout = binding.passwordField

        val emailEditText: TextInputEditText = binding.email
        val passwordEditText: TextInputEditText = binding.password

        val loginButton: Button = binding.loginBtn

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInputs(emailLayout, passwordLayout, email, password)) {
                loginUser(email, password)
            }
        }
        // Check if the user is already logged in
        if (isLoggedIn()) {
            redirectToHomeScreen()
        }
        return view
    }

    private fun validateInputs(
        emailLayout: TextInputLayout,
        passwordLayout: TextInputLayout,
        email: String,
        password: String
    ): Boolean {
        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            return false
        }

        // Reset errors if all inputs are valid
        emailLayout.error = null
        passwordLayout.error = null

        return true
    }

    private fun isPasswordValid(email: String, password: String): CompletableFuture<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()

        // Ambil data pengguna dari Firestore berdasarkan email
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val savedPassword = document.getString("password")
                    // Bandingkan password yang diinputkan dengan password yang disimpan
                    if (savedPassword != null && savedPassword == password) {
                        completableFuture.complete(true) // Password cocok
                        return@addOnSuccessListener
                    }
                }
                completableFuture.complete(false) // Password tidak cocok
            }
            .addOnFailureListener { e ->
                // Gagal mengambil data pengguna
                showError("Failed to retrieve user data")
                completableFuture.complete(false)
            }
        return completableFuture
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Login berhasil
                    isPasswordValid(email, password).thenApply { isValid ->
                        if (isValid) {
                            saveLoginStatus(true)
                            redirectToHomeScreen()
                        } else {
                            // Password tidak cocok
                            saveLoginStatus(false)
                            showError("Invalid email or password")
                        }
                    }
                } else {
                    // Login gagal
                    saveLoginStatus(false)
                    showError("Invalid email or password")
                    // Tambahkan logika sesuai kebutuhan setelah login gagal
                }
            }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // Simpan status login ke SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun isLoggedIn(): Boolean {
        // Periksa status login dari SharedPreferences
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun redirectToHomeScreen() {
        val intentToHomeScreen = Intent(requireContext(), HomeScreenActivity::class.java)
        startActivity(intentToHomeScreen)
        requireActivity().finish()
    }
    private fun showError(errorMessage: String) {
        // Tampilkan pesan kesalahan kepada pengguna
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}