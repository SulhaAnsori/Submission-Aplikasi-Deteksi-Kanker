package com.dicoding.asclepius.helper

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Predict(
    val imageUri : Uri,
    val label : String,
    val score : Float
) : Parcelable
