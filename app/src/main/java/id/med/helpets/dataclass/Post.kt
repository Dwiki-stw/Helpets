package id.med.helpets.dataclass

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Post(
    var id: String? = "",
    var name: String? = "",
    var description: String? = "",
    var photoUrl: String? = "",
    var category: String? = "",
    var address: String? = "",
    var lat: Double? = 0.0,
    var lon: Double? = 0.0,
    var date: Long ? = 0,
    var isFavorite: Boolean = false
): Parcelable
