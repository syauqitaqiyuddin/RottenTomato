package com.example.rottentomato

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rottentomato.databinding.MovieDetailBinding

class MovieDetailActivity : AppCompatActivity() {
    private var binding: MovieDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Retrieve data from the intent
        val bundle = intent.extras
        if (bundle != null) {
            val title = bundle.getString("movieTitle", "")
            val director = bundle.getString("movieDirector", "")
            val genre = bundle.getString("movieGenre", "")
            val deskripsi = bundle.getString("movieDeskripsi", "")
            val posterUrl = bundle.getString("moviePoster", "")

            // Set data to views
            binding!!.title.text = title
            binding!!.director.text = director
            binding!!.genre.text = genre
            binding!!.deskripsi.text = deskripsi

            // Load poster image using Glide
            Glide.with(this).load(posterUrl).into(
                binding!!.image
            )
        }
    }
}