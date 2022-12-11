package id.med.helpets.ui.detail

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.med.helpets.R
import id.med.helpets.adapter.FavoriteAdapter.Companion.removeToFavorite
import id.med.helpets.databinding.ActivityDetailBinding
import id.med.helpets.dataclass.Post
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var firebaseAuth: FirebaseAuth



    private var isInMyFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favorite()

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null){
            checkIsFavorite()
        }

        val postItem = intent.getParcelableExtra<Post>(EXTRA_POST) as Post

        val date = postItem.date
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val formattedDate = format.format(date)
        id = postItem.id!!
        name = postItem.name!!
        address = postItem.address!!
        photoUrl = postItem.photoUrl!!
        isFavorite = postItem.isFavorite.toString()!!




        Glide.with(this@DetailActivity)
            .load(postItem.photoUrl.toString())
            .into(binding.detailImg)
        binding.apply {
            binding.detailPetsUploadedTime.text = formattedDate
            binding.detailPetsAddress.text = postItem.address
            binding.detailPetsDescription.text = postItem.description
            binding.detailPetsUsername.text = postItem.name


        }

    }

    private fun favorite() {
        binding.detailSavePets.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "you're not logged in", Toast.LENGTH_SHORT).show()

            } else {
                if (isInMyFavorite){
                    removeToFavorite(this, id)
                } else {
                    addToFavorite()
                }




            }
        }
    }

    private fun addToFavorite(){
        Log.d(TAG,"addToFavorite to fav")


            val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["favId"] = id
        hashMap["name"] = name
        hashMap["timestamp"] = timestamp
        hashMap["address"] = address
        hashMap["photoUrl"] = photoUrl

        val ref = FirebaseDatabase.getInstance().getReference("DataUser")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(id)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"addToFavorite to fav")
            }
            .addOnFailureListener {
                Log.d(TAG,"addToFavorite failed fav due to ${it.message}")
               Toast.makeText(this,"Failed to add to fav due to ${it.message} ",Toast.LENGTH_SHORT).show()
            }

    }
//    private fun removeToFavorite(){
//        Log.d(TAG,"remove from favorite")
//
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(firebaseAuth.uid!!).child("Favorites").child(id)
//            .removeValue()
//            .addOnSuccessListener {
//                Log.d(TAG,"remove from favorite: Removed from fav")
//            }
//            .addOnFailureListener { e->
//                Log.d(TAG,"remove from favorite: Failed to remove from fav due to ${e.message}")
//                Toast.makeText(this,"Failed to remove to tfav due to ${e.message} ",Toast.LENGTH_SHORT).show()
//            }
//    }
    private fun checkIsFavorite(){
        Log.d(TAG,"checkIsFavorite: Checking if book is in fav or not")
        val ref = FirebaseDatabase.getInstance().getReference("DataUser")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if(isInMyFavorite){
                        Log.d(TAG, "onDataChange: available in favorite")
                        binding.detailSavePets.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite,0,0)

                    } else {
                        Log.d(TAG, "onDataChange: not available in favorite")
                        binding.detailSavePets.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_notfavorite,0,0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }


    companion object {
        const val EXTRA_POST = "extra_post"
        private var id = "id"
        private var name = "name"
        private var photoUrl = "phoroUrl"
        private var address = "address"
        private var isFavorite = "false"
    }
}