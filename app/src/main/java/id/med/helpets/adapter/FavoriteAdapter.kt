package id.med.helpets.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.med.helpets.DetailForFavoriteActivity
import id.med.helpets.databinding.FragmentDashboardBinding
import id.med.helpets.databinding.ItemRowUserBinding
import id.med.helpets.databinding.ItmRowFavoriteBinding
import id.med.helpets.dataclass.Post
import id.med.helpets.ui.detail.DetailActivity
import java.sql.Timestamp
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.HolderPetFavorite> {

    private val context: Context
    private var petsArray: ArrayList<Post>


    private lateinit var binding: ItmRowFavoriteBinding

    constructor(context: Context, petsArray: ArrayList<Post>) {
        this.context = context
        this.petsArray = petsArray
    }

    inner class HolderPetFavorite(itemView: View): RecyclerView.ViewHolder(itemView){
        var ivStory = binding.ivStory
        var tvUsername = binding.tvUsername
        var tvAddress = binding.tvAddress
        var btnFav = binding.btnFavorite


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPetFavorite {
        binding = ItmRowFavoriteBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderPetFavorite(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPetFavorite, position: Int) {
        val model = petsArray[position]

        loadPetDetails(model,holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailForFavoriteActivity::class.java)
            intent.putExtra("favId",model.id)
            intent.putExtra("name",model.name)
            intent.putExtra("address",model.address)
            intent.putExtra("photoUrl",model.photoUrl)
            intent.putExtra("description",model.description)
//            intent.putExtra("lat",model.lat)
//            intent.putExtra("lon",model.lon)
          //  intent.putExtra("photoUrl",model.photoUrl)

            context.startActivity(intent)
        }

        holder.btnFav.setOnClickListener {
            removeToFavorite(context, model.id.toString())
        }
        holder.tvUsername.text = model.name
        holder.tvAddress.text = model.address
        Glide.with(holder.itemView.context)
            .load(model.photoUrl)
            .into(binding.ivStory)
    }

    private fun loadPetDetails(model: Post, holder: HolderPetFavorite) {
        val petId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("post")
        ref.child(petId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val id = "${snapshot.child("id").value}"
                    val address = "${snapshot.child("address").value}"
                    val category = "${snapshot.child("category").value}"
//                    val date = "${snapshot.child("date").value}"
                    val description = "${snapshot.child("description").value}"
                    val lat = "${snapshot.child("lat").value}"
                    val lon = "${snapshot.child("lon").value}"
                    val name = "${snapshot.child("name").value}"
                    val photoUrl = "${snapshot.child("photoUrl").value}"

                    model.isFavorite = true
                    model.name = name
                    model.id = id
//                    model.date = date.toLong()
                    model.address = address
                    model.category = category
                    model.description = description
//                    model.lat = lat.toDouble()
//                    model.lon = lon.toDouble()
                    model.photoUrl = photoUrl






                    loadPetDetails(model, holder)










                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getItemCount(): Int {
        return petsArray.size
    }
    companion object{
        fun formatTimeStamp (timestamp: Long): String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return android.text.format.DateFormat.format("dd/MM/yyyy",cal).toString()
        }

        public fun removeToFavorite(context: Context, petId: String){
            val TAG = "REMOVE_FAV_TAG"
            Log.d(TAG,"remove from favorite")

            val firebaseAuth = FirebaseAuth.getInstance()

            val ref = FirebaseDatabase.getInstance().getReference("DataUser")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(petId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(TAG,"remove from favorite: Removed from fav")
                }
                .addOnFailureListener { e->
                    Log.d(TAG,"remove from favorite: Failed to remove from fav due to ${e.message}")
                    Toast.makeText(context,"Failed to remove to tfav due to ${e.message} ", Toast.LENGTH_SHORT).show()
                }
        }
    }
}