package id.med.helpets.dataclass

import android.accessibilityservice.GestureDescription.StrokeDescription
import android.os.Parcelable
import id.med.helpets.ui.login.customview.EditTextPassword
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name: String? = null,
    var email: String? = null,
    var nomorTelp: String? = null,
    var alamat: String? = null,
    var password: String? = null
): Parcelable
