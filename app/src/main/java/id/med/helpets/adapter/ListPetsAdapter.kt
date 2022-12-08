package id.med.helpets.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import id.med.helpets.R
import id.med.helpets.databinding.ItemRowUserBinding
import id.med.helpets.dataclass.Post
import id.med.helpets.ui.detail.DetailActivity

class ListPetsAdapter (options: FirebaseRecyclerOptions<Post>): FirebaseRecyclerAdapter<Post, ListPetsAdapter.PetsViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_row_user, parent, false)
        val binding = ItemRowUserBinding.bind(view)
        return PetsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PetsViewHolder,
        position: Int,
        model: Post
    ) {
        val result = getItem(position)
        holder.bind(result)
        val itemView = holder.itemView
        itemView.setOnClickListener {
            toDetailPost(itemView, model)
        }
    }

    private fun toDetailPost(itemView: View, data: Post) {
        val intent = Intent (itemView.context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_POST, data)
        itemView.context.startActivity(intent)
    }

    inner class PetsViewHolder(private val binding: ItemRowUserBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Post) {
            binding.tvUsername.text = item.name
            binding.tvAddress.text = item.address
            Log.d("PPPP", item.name.toString())
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(binding.ivStory)
        }
    }
}