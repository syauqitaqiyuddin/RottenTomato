package com.example.rottentomato.Local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val judul_film: String,
    val genre: String,
    val direktor: String,
    val posterUrl: String,
    val deskripsi: String
)
