package com.example.rottentomato

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.example.rottentomato.database.MovieData
import com.example.rottentomato.databinding.TambahFilmBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class TambahFilmActivity : AppCompatActivity() {
    private lateinit var binding: TambahFilmBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Storage dan Firestore
        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        val imageView: ImageView = findViewById(R.id.img_movieBtn)
        imageView.setOnClickListener {
            openFileChooser()
        }

        val addBtn: MaterialButton = findViewById(R.id.addNewBtn)
        addBtn.setOnClickListener {
            // ... (logika untuk menambahkan data ke Firebase, termasuk URL gambar)
            uploadFile()
        }
    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val imageView: ImageView = findViewById(R.id.img_movieBtn)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile() {
        if (imageUri != null) {
            val fileReference = storageReference.child("uploads/" + UUID.randomUUID().toString())
            fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        // Dapatkan URL gambar setelah diunggah
                        val imageUrl = uri.toString()

                        // Simpan data ke Firebase Firestore (db)
                        saveMovieData(imageUrl)
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun saveMovieData(imageUrl: String) {
        // Dapatkan referensi ke koleksi "movies" pada Firebase Firestore
        val moviesCollection = db.collection("movies")

        // ... (ambil data lainnya dari input fields)
        val titleEditText: TextInputEditText = findViewById(R.id.title_add)
        val genreEditText: TextInputEditText = findViewById(R.id.genre_add)
        val directorEditText: TextInputEditText = findViewById(R.id.director_add)
        val descriptionEditText: TextInputEditText = findViewById(R.id.deskripsi_add)

        // Buat objek MovieData
        val movieData = MovieData(
            judul_film = titleEditText.text.toString(),
            genre = genreEditText.text.toString(),
            direktor = directorEditText.text.toString(),
            deskripsi = descriptionEditText.text.toString(),
            posterUrl = imageUrl
        )

        // Tambahkan data ke Firebase Firestore
        moviesCollection.add(movieData)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}