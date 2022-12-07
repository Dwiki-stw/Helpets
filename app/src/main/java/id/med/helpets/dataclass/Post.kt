package id.med.helpets.dataclass

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val category: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val date: Long
)
