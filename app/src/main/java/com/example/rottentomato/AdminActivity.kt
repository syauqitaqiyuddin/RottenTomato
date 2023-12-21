package com.example.rottentomato

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rottentomato.databinding.AdminBinding
import com.example.rottentomato.databinding.HomeScreenBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: AdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val addBtn: FloatingActionButton = findViewById(R.id.addBtn)
        addBtn.setOnClickListener {
            // Handle klik tombol "Add Movie" di sini
            val intent = Intent(this, TambahFilmActivity::class.java)
            startActivity(intent)
        }
    }
}