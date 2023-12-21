package com.example.rottentomato

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rottentomato.database.MovieData
import com.example.rottentomato.databinding.EditFilmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EditFilmActivity : AppCompatActivity() {
    private lateinit var binding: EditFilmBinding

    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("movies")
    private lateinit var storageReference: StorageReference
    private lateinit var movieToEdit: MovieData
    private lateinit var id: String
    private lateinit var oldImageURL: String

    private var selectedImageUri: Uri? = null

    companion object {
        private const val GALLERY_REQUEST_CODE = 1  // Changed from string to integer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        // ...

        // Get movieId from Intent
        id = intent.getStringExtra("movieToEdit").toString()

        // Load movie data based on movieId
        loadMovieData(id)

        // Set click listeners
        binding.deleteBtn.setOnClickListener { onDeleteButtonClick() }
        binding.updateBtn.setOnClickListener { onUpdateButtonClick() }
        binding.editImgMovie.setOnClickListener { openGallery() }
    }

    private fun loadMovieData(movieId: String) {
        moviesCollection.document(movieId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Convert document data to MovieData object
                    movieToEdit = document.toObject(MovieData::class.java)!!

                    // Populate UI fields
                    binding.editTitle.setText(movieToEdit.judul_film)
                    binding.editGenre.setText(movieToEdit.genre)
                    binding.editDirector.setText(movieToEdit.direktor)
                    binding.editDeskripsi.setText(movieToEdit.deskripsi)
                    oldImageURL = movieToEdit.posterUrl

                    // Load image using Glide or your preferred image loading library
                    Glide.with(this).load(movieToEdit.posterUrl).into(binding.editImgMovie)
                } else {
                    // Handle the case where the document is null
                    Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show()
                    finish()  // Close the activity if movie is not found
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures
                Toast.makeText(this, "Error loading movie data: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onDeleteButtonClick() {
        // Implement delete operation here
        moviesCollection.document(id)
            .delete()
            .addOnSuccessListener {
                // Handle successful deletion
                Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                // Handle deletion failure
                Toast.makeText(this, "Error deleting movie: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onUpdateButtonClick() {
        // Implement update operation here
        val newTitle = binding.editTitle.text.toString()
        val newGenre = binding.editGenre.text.toString()
        val newDirector = binding.editDirector.text.toString()
        val newDeskripsi = binding.editDeskripsi.text.toString()

        // Update movie data
        val updatedMovie = MovieData(id, newTitle, newGenre, newDirector, newDeskripsi, oldImageURL)

        moviesCollection.document(id)
            .set(updatedMovie)
            .addOnSuccessListener {
                // Handle successful update
                Toast.makeText(this, "Movie updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                // Handle update failure
                Toast.makeText(this, "Error updating movie: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get selected image URI
            selectedImageUri = data.data

            // Update the image view using Glide or your preferred image loading library
            Glide.with(this).load(selectedImageUri).into(binding.editImgMovie)
        }
    }
}

