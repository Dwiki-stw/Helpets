package id.med.helpets.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.med.helpets.R
import id.med.helpets.adapter.FavoriteAdapter
import id.med.helpets.adapter.ListPetsAdapter
import id.med.helpets.databinding.FragmentDashboardBinding
import id.med.helpets.dataclass.Post

class DashboardFragment : Fragment() {

private var _binding: FragmentDashboardBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapterFavorite: FavoriteAdapter

    private lateinit var petsList : ArrayList<Post>

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDashboardBinding.inflate(inflater, container, false)

      firebaseAuth = FirebaseAuth.getInstance()
      loadFavoritePet()


    return binding.root



  }

    private fun loadFavoritePet() {
        petsList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("DataUser")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    petsList.clear()
                    for (ds in snapshot.children){
                        val favId = "${ds.child("favId").value}"
                        val name = "${ds.child("name").value}"
                        val address = "${ds.child("address").value}"
                        val photoUrl = "${ds.child("photoUrl").value}"
                        val modelPet = Post()
                        modelPet.id = favId
                        modelPet.name = name
                        modelPet.photoUrl = photoUrl
                        modelPet.address = address

                        petsList.add(modelPet)
                    }


                        val manager = LinearLayoutManager(requireContext())
                        manager.reverseLayout = true
                        manager.stackFromEnd = true

                        adapterFavorite = FavoriteAdapter(requireContext(),petsList)

                            binding.listFavorite.layoutManager = manager
                           binding.listFavorite.adapter = adapterFavorite



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adapterFavorite = FavoriteAdapter(requireContext(),petsList)
//        binding.listFavorite.adapter = adapterFavorite

//        if (binding != null && binding.listFavorite != null) {
//             adapterFavorite = FavoriteAdapter(requireContext(),petsList)
//            binding.listFavorite.adapter = adapterFavorite
//        }

    }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }




}