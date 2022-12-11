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
import id.med.helpets.UpdateActivity
import id.med.helpets.databinding.FragmentProfileBinding
import id.med.helpets.dataclass.User
import id.med.helpets.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var user: User
    private var name = ""
    private var email = ""
    private var nomorTelp = ""
    private var alamat = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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
            activity?.finish()
        }

        binding.btnUpdate.setOnClickListener {
            //startActivity(Intent(context, UpdateActivity::class.java))
            name = binding.edtNama.text.toString()
            email = binding.edtEmail.text.toString()
            nomorTelp = binding.edtNomor.text.toString()
            alamat = binding.edtAlamat.text.toString()
            password = binding.edtPassword.text.toString()

            updateData(name, email, nomorTelp, alamat, password)
        }
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
                binding.edtNama.setText(user.name)
                binding.edtEmail.setText(user.email)
                binding.edtNomor.setText(user.nomorTelp)
                binding.edtAlamat.setText(user.alamat)
                binding.edtPassword.setText(user.password)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateData(name: String, email: String, nomorTelp: String, Alamat: String, Password: String) {
        //val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")
        val user = mapOf<String, String>(
            //"uid" to uid,
            "name" to name,
            "email" to email,
            "nomorTelepon" to nomorTelp,
            "alamat" to Alamat,
            "password" to Password
        )

        databaseReference.child(uid).updateChildren(user).addOnSuccessListener {
            binding.edtNama.text?.clear()
            binding.edtEmail.text?.clear()
            binding.edtNomor.text?.clear()
            binding.edtAlamat.text?.clear()
            binding.edtPassword.text?.clear()
            Toast.makeText(context, "Berhasil Diubah", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal Diubah", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

    }
}