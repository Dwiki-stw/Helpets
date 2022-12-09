package id.med.helpets.ui.home

import android.os.Bundle
import android.os.Handler
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.adapter.ListPetsAdapter
import id.med.helpets.databinding.FragmentHomeBinding
import id.med.helpets.dataclass.Post
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.concurrent.CountDownLatch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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

//      val firebaseUser = auth.currentUser
//
//      val name = firebaseUser?.displayName
//
//      binding.tvDisplayUsername.text = name

      auth = Firebase.auth
      db = Firebase.database
      val petsRef = db.reference.child(MESSAGES_CHILD)

      val manager = LinearLayoutManager(context)
      manager.reverseLayout = true
      manager.stackFromEnd = true

      addPets()

      binding.rvPets.itemAnimator = null

      binding.chipNearest.isFocusedByDefault
      binding.chipGroup.isSelectionRequired = true;
      binding.chipNearest.isChecked = true

      binding.chipNearest.setOnClickListener {
          showLoading(true)
          petsRef.addListenerForSingleValueEvent( object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  setUpRecycleView(snapshot)
                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      binding.chipDogs.setOnClickListener {
          showLoading(true)
          petsRef.orderByChild("category").equalTo("dog").addListenerForSingleValueEvent( object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  setUpRecycleView(snapshot)
                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      binding.chipCats.setOnClickListener {
          showLoading(true)
          petsRef.orderByChild("category").equalTo("cat").addListenerForSingleValueEvent( object :ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  setUpRecycleView(snapshot)
                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      return root
  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        db = Firebase.database
        val petsRef = db.reference.child(MESSAGES_CHILD)
        showLoading(true)
        petsRef.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                setUpRecycleView(snapshot)
                showLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setUpRecycleView(snapshot: DataSnapshot) {
        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        val adapter: ListPetsAdapter = ListPetsAdapter(snapshot)

        binding.rvPets.layoutManager = manager
        binding.rvPets.adapter = adapter
        binding.rvPets.itemAnimator = null
    }

    private fun addPets(){
        binding.addPets.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_createPostActivity)
        }
    }

    private fun showLoading(visibility: Boolean) {
        if (visibility) {
            binding.loadingHome.visibility = View.VISIBLE
        } else {
            binding.loadingHome.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MESSAGES_CHILD = "post"
    }
}