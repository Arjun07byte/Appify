package com.appify.appifymain.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AppInfo(
    val appInfoPicture: String = "null",
    val description: String = "null",
    val gif: String = "null",
    val name: String = "null",
    val price: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val thumbnail: String = "null"
) : Parcelable