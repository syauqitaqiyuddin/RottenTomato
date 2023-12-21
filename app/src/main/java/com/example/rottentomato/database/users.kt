package com.example.rottentomato.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

data class users(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = ""
)
