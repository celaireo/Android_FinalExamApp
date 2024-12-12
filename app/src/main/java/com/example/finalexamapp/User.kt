package com.example.finalexamapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String // Ajout de la virgule ici
)
