package id.med.helpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import id.med.helpets.databinding.ActivityDetailBinding
import id.med.helpets.databinding.ActivityDetailForFavoriteBinding

class DetailForFavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailForFavoriteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailForFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")
        val description = intent.getStringExtra("description")
        val photoUrl = intent.getStringExtra("photoUrl")

        binding.detailPetsUsername.text = name
        binding.detailPetsAddress.text = address.toString()
        binding.detailPetsDescription.text = description
        Glide.with(this@DetailForFavoriteActivity)
            .load(photoUrl)
            .into(binding.detailImg)






    }
    companion object {
        const val EXTRA_POST = "extra_post"
    }
}