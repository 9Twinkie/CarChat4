package com.example.carchat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val email: String,
    val password: String
) : Parcelable
