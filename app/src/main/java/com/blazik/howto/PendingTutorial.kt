package com.blazik.howto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class PendingTutorial(
    var key: String? = null,
    val mainCategory: String = "",
    val subCategory: String = "",
    val videoUrl: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val mainPos: Int = 0,
    val subPos: String = "",
    val status: String = "pending"
) : Parcelable