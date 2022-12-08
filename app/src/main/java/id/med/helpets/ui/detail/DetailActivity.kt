package id.med.helpets.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import id.med.helpets.R
import id.med.helpets.databinding.ActivityDetailBinding
import id.med.helpets.dataclass.Post

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postItem = intent.getParcelableExtra<Post>(EXTRA_POST) as Post

        Glide.with(this@DetailActivity)
            .load(postItem.photoUrl.toString())
            .into(binding.detailImg)
        binding.apply {
            binding.detailPetsUploadedTime.text = postItem.date.toString()
            binding.detailPetsAddress.text = postItem.address
            binding.detailPetsDescription.text = postItem.description
            binding.detailPetsUsername.text = postItem.name
        }
    }

    companion object {
        const val EXTRA_POST = "extra_post"
    }
}