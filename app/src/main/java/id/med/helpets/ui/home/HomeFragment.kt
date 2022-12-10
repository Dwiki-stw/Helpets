package id.med.helpets.ui.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.dataclass.User
import id.med.helpets.adapter.ListPetsAdapter
import id.med.helpets.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ListPetsAdapter
    private val binding get() = _binding!!
    private lateinit var uid: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

      _binding = FragmentHomeBinding.inflate(inflater, container, false)

      val root: View = binding.root

      auth = Firebase.auth
      db = Firebase.database
      val petsRef = db.reference.child(MESSAGES_CHILD)

      val manager = LinearLayoutManager(context)
      manager.reverseLayout = true
      manager.stackFromEnd = true

      uid = auth.currentUser?.uid.toString()

      databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")

      if (uid.isNotEmpty()) {
          getDataUser()
      } else {
          Toast.makeText(context, "Error: Failed", Toast.LENGTH_SHORT).show()
      }

      addPets()

      binding.rvPets.itemAnimator = null

      binding.chipNearest.isFocusedByDefault
      binding.chipGroup.isSelectionRequired = true;
      binding.chipNearest.isChecked = true

      binding.chipNearest.setOnClickListener {
          showLoading(true)
          petsRef.addListenerForSingleValueEvent( object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {

                  manager.reverseLayout = true
                  manager.stackFromEnd = true

                  adapter = ListPetsAdapter(snapshot)

                  binding.rvPets.layoutManager = manager
                  binding.rvPets.adapter = adapter
                  binding.rvPets.itemAnimator = null

                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      binding.chipDogs.setOnClickListener {
          showLoading(true)
          petsRef.orderByChild("category").equalTo("DOG").addListenerForSingleValueEvent( object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {

                  manager.reverseLayout = true
                  manager.stackFromEnd = true

                  adapter = ListPetsAdapter(snapshot)

                  binding.rvPets.layoutManager = manager
                  binding.rvPets.adapter = adapter
                  binding.rvPets.itemAnimator = null

                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      binding.chipCats.setOnClickListener {
          showLoading(true)
          petsRef.orderByChild("category").equalTo("CAT").addListenerForSingleValueEvent( object :ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {

                  manager.reverseLayout = true
                  manager.stackFromEnd = true

                  adapter = ListPetsAdapter(snapshot)

                  binding.rvPets.layoutManager = manager
                  binding.rvPets.adapter = adapter
                  binding.rvPets.itemAnimator = null

                  showLoading(false)
              }

              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
              }

          })
      }

      return root
  }

    private fun getDataUser() {
        showLoading(true)
        val id = auth.currentUser?.uid.toString()
        databaseReference.child(id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                user = snapshot.getValue(User::class.java)!!
                binding.tvDisplayUsername.setText(user.name)
            }

            override fun onCancelled(error: DatabaseError) {
                showLoading(false)
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        db = Firebase.database
        val petsRef = db.reference.child(MESSAGES_CHILD)
        showLoading(true)
        petsRef.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val manager = LinearLayoutManager(context)
                manager.reverseLayout = true
                manager.stackFromEnd = true

                adapter = ListPetsAdapter(snapshot)

                binding.rvPets.layoutManager = manager
                binding.rvPets.adapter = adapter
                binding.rvPets.itemAnimator = null
                showLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }

        })
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