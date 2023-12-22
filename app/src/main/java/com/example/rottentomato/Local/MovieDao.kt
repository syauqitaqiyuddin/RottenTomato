package com.example.rottentomato.Local

import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(movies: List<MovieEntity>): List<Long>

    @Transaction
    fun insertMovies(localmovies: List<MovieEntity>) {
        val existingMovies = getAllMovies()
        val moviesToInsert = localmovies.filter { newMovie ->
            existingMovies.none { it.judul_film == newMovie.judul_film && it.direktor == newMovie.direktor && it.deskripsi == newMovie.deskripsi } // memeriksa duplikat
        }
        val insertedRowIds = insertAll(moviesToInsert)
        Log.d("LocalDatabase", "Inserted ${insertedRowIds.size} rows into local database")
    }
}