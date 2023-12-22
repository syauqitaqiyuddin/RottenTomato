package com.example.rottentomato

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.example.rottentomato.databinding.ProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        // Find the logout button by its ID
        val logoutButton: AppCompatButton = binding.logoutBtn

        // Set the OnClickListener for the logout button
        logoutButton.setOnClickListener {
            // Call the logout function
            performLogout()
        }
        return view
    }
    private fun performLogout() {
        // Sign out the user from Firebase Authentication
        auth.signOut()

        // Redirect to the login screen (MainActivity)
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}