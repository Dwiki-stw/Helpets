package id.med.helpets.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.databinding.FragmentHomeBinding
import id.med.helpets.databinding.FragmentProfileBinding
import id.med.helpets.dataclass.User
import id.med.helpets.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private val binding get() = _binding!!
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")

        auth = Firebase.auth
        db = Firebase.database

        uid = auth.currentUser?.uid.toString()

        if (uid.isNotEmpty()) {
            getDataUser()
        } else {
            Toast.makeText(context, "Error: Failed", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }

//        name = binding.edtNama.text.toString()
//        email = binding.edtEmail.text.toString()
//        nomorTelp = binding.edtNomor.text.toString()
//        alamat = binding.edtAlamat.text.toString()
//        password = binding.edtPassword.text.toString()

        return root
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun getDataUser() {
        val id = auth.currentUser?.uid.toString()
        databaseReference.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.txtName.setText(user.name)
                binding.edtNama.setText(user.name)
                binding.edtEmail.setText(user.email)
                binding.edtNomor.setText(user.nomorTelp)
                binding.edtAlamat.setText(user.alamat)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}