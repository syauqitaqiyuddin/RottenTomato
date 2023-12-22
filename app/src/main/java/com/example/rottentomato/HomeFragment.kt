package com.example.rottentomato

import android.content.Context
import android.graphics.Movie
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rottentomato.Adapter.HomeFragmentAdapter
import com.example.rottentomato.database.MovieData
import com.example.rottentomato.databinding.HomeFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var homefragmentAdapter: HomeFragmentAdapter
    private lateinit var dataList: MutableList<MovieData>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var context: Context
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(layoutInflater)
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        context = requireContext()

        val gridLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rVhome.layoutManager = gridLayoutManager

        dataList = mutableListOf<MovieData>()

        homefragmentAdapter = HomeFragmentAdapter(requireContext(), dataList)
        binding.rVhome.adapter = homefragmentAdapter

        // Fetch movie data from Firestore
        firestore.collection("movies").get()
            .addOnSuccessListener { result ->
                dataList.clear()
                for (document in result) {
                    val dataClass = document.toObject(MovieData::class.java)
                    dataList.add(dataClass)
                }
                homefragmentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle failure
                Log.e("HomeFragment", "Failed to fetch movie data: $it")
            }

        // Set username from Firebase Authentication
        val display: TextView = binding.displayUsername
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val username = documentSnapshot.getString("username")
                        display.text = "$username"
                    } else {
                        // Handle the case where the document doesn't exist
                        display.text = "User"
                    }
                }
                .addOnFailureListener {
                    // Handle failures
                    display.text = "User" // Provide a default message
                }
        } else {
            // Handle the case where the user is not signed in
            display.text = "User" // Provide a default message
        }

        return binding.root
    }


}
