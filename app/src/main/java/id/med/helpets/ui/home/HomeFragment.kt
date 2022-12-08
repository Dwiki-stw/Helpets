package id.med.helpets.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.adapter.ListPetsAdapter
import id.med.helpets.databinding.FragmentHomeBinding
import id.med.helpets.dataclass.Post

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var viewModel: HomeViewModel
    private lateinit var petsRecycleView: RecyclerView
    private lateinit var adapter: ListPetsAdapter
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

      _binding = FragmentHomeBinding.inflate(inflater, container, false)

      val root: View = binding.root

      auth = Firebase.auth

      val bundle = arguments
      val name = bundle!!.getString("name")

      db = Firebase.database

      val petsRef = db.reference.child(MESSAGES_CHILD)

      val options = FirebaseRecyclerOptions.Builder<Post>()
          .setQuery(petsRef, Post::class.java)
          .build()

      val manager = LinearLayoutManager(context)
      manager.reverseLayout = true
      manager.stackFromEnd = true

      addPets()

      binding.rvPets.layoutManager = manager

      adapter = ListPetsAdapter(options)
      binding.rvPets.adapter =  adapter
      binding.rvPets.itemAnimator = null

      binding.chipNearest.isFocusedByDefault

      binding.chipDogs.setOnClickListener {
          Toast.makeText(context, "WOII ANJING", Toast.LENGTH_SHORT).show()
      }


      return root
  }

    private fun addPets(){
        binding.addPets.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_createPostActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sortChipDogs() {

    }

    companion object {
        const val MESSAGES_CHILD = "post"
    }
}