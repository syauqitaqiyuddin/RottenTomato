package com.example.rottentomato

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.rottentomato.database.MovieData
import com.example.rottentomato.databinding.AddFilmBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class AddFilmActivity : AppCompatActivity() {
    private lateinit var binding: AddFilmBinding
    private lateinit var storageReference: StorageReference
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val moviesCollection = db.collection("movies")
    private val storage = FirebaseStorage.getInstance()

    private var selectedImageUri: Uri? = null

    companion object {
        private const val GALLERY_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = storage.reference

        binding.imgMovieBtn.setOnClickListener {
            openGallery()
        }

        binding.addNewBtn.setOnClickListener {
            addMovie()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun addMovie() {
        val judul = binding.titleAdd.text.toString().trim()
        val genre = binding.genreAdd.text.toString().trim()
        val direktor = binding.directorAdd.text.toString().trim()
        val deskripsi = binding.deskripsiAdd.text.toString().trim()

        if (judul.isEmpty() || genre.isEmpty() || direktor.isEmpty() || deskripsi.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Lengkapi semua informasi", Toast.LENGTH_SHORT).show()
            return
        }

        val movieId = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$movieId.jpg")

        // Upload gambar ke Firebase Storage
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { _ ->
                // Dapatkan URL gambar yang diunggah
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Buat objek MovieData
                    val movie = MovieData(movieId, judul, genre, direktor, deskripsi, uri.toString())

                    // Simpan data film ke Firebase Firestore
                    moviesCollection.document(movieId)
                        .set(movie)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Film berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal menambahkan film: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengunggah gambar: $e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data

            // Tampilkan gambar yang dipilih di ImageView
            binding.imgMovieBtn.setImageURI(selectedImageUri)
        }
    }
}