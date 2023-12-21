package com.example.rottentomato

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rottentomato.databinding.RegisterFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterFragment : Fragment() {
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASS = "extra_password"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        val usersCollection = db.collection("users")

        val usernameLayout: TextInputLayout = view.findViewById(R.id.username_field)
        val emailLayout: TextInputLayout = view.findViewById(R.id.email_field)
        val passwordLayout: TextInputLayout = view.findViewById(R.id.password_field)
        val confirmPasswordLayout: TextInputLayout = view.findViewById(R.id.password_field_confirm)

        val usernameEditText: TextInputEditText = view.findViewById(R.id.username)
        val emailEditText: TextInputEditText = view.findViewById(R.id.email)
        val passwordEditText: TextInputEditText = view.findViewById(R.id.password)
        val confirmPasswordEditText: TextInputEditText = view.findViewById(R.id.confirm_password)

        val registerButton: Button = view.findViewById(R.id.register_btn)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

//             Validasi input sebelum mendaftarkan pengguna
            if (validateInputs(
                    usernameLayout,
                    emailLayout,
                    passwordLayout,
                    confirmPasswordLayout,
                    username,
                    email,
                    password,
                    confirmPassword
                )
            ) {
                // Lakukan pendaftaran pengguna
                registerUser(username,email, password)
            }
        }
        return view
    }
    private fun validateInputs(
        usernameLayout: TextInputLayout,
        emailLayout: TextInputLayout,
        passwordLayout: TextInputLayout,
        confirmPasswordLayout: TextInputLayout,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            usernameLayout.error = "Username is required"
            return false
        }
        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            return false
        }

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            confirmPasswordLayout.error = "Passwords do not match"
            return false
        }

        // Reset errors if all inputs are valid
        usernameLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null

        return true
    }
    private fun registerUser(username: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Logika setelah registrasi berhasil
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    // Simpan data pengguna ke Firestore
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password
                    )
                    db.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            // Data pengguna berhasil disimpan
                            redirectToHomeScreen(username, email, password)
                        }
                        .addOnFailureListener {
                            showError("Registration failed")
                        }
                } else {
                    // Registrasi gagal
                    // Tambahkan logika sesuai kebutuhan setelah registrasi gagal
                }
            }
    }
    private fun redirectToHomeScreen(name: String, email: String, password: String, clearTask: Boolean = true) {
        val intentToHomeScreen = Intent(requireContext(), HomeScreenActivity::class.java)
        intentToHomeScreen.putExtra(EXTRA_USERNAME, name)
        intentToHomeScreen.putExtra(EXTRA_EMAIL, email)
        intentToHomeScreen.putExtra(EXTRA_PASS, password)

        if (clearTask) {
            intentToHomeScreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intentToHomeScreen)

        if (clearTask) {
            requireActivity().finish()
        }
    }
    private fun showError(errorMessage: String) {
        // Tampilkan pesan kesalahan kepada pengguna
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}
