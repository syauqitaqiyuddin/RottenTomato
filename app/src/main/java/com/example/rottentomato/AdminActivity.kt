package com.example.rottentomato

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rottentomato.Adapter.MovieAdapter
import com.example.rottentomato.database.MovieData
import com.example.rottentomato.databinding.AdminBinding
import com.example.rottentomato.databinding.HomeScreenBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: AdminBinding
    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("movies")
    private val movieList = mutableListOf<MovieData>()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieAdapter = MovieAdapter(this, movieList)
        binding.recycleView.layoutManager = GridLayoutManager(this, 1)
        binding.recycleView.adapter = movieAdapter

        val signOutBtn: TextView = binding.signOutBtn
        signOutBtn.setOnClickListener {
            signOut()
        }

        // Ambil data dari Firestore
        moviesCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val movie = document.toObject(MovieData::class.java)
                    movieList.add(movie)
                }
                movieAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: $e", Toast.LENGTH_SHORT).show()
            }


        val addBtn: FloatingActionButton = findViewById(R.id.addBtn)
        addBtn.setOnClickListener {
            // Handle klik tombol "Add Movie" di sini
            val intent = Intent(this, AddFilmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOut() {
        // Hapus sesi login dari SharedPreferences
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Logout dari Firebase
        FirebaseAuth.getInstance().signOut()

        // Kembali ke MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
