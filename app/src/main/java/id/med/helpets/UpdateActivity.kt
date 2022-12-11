package id.med.helpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.databinding.ActivityRegisterBinding
import id.med.helpets.databinding.ActivityUpdateBinding
import id.med.helpets.dataclass.User

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var uid: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private var name = ""
    private var email = ""
    private var nomorTelp = ""
    private var alamat = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Update Profile"

        databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")

        auth = Firebase.auth
        db = Firebase.database

        uid = auth.currentUser?.uid.toString()

        binding.btnUpdate.setOnClickListener {
            name = binding.edtNama.text.toString()
            email = binding.edtEmail.text.toString()
            nomorTelp = binding.edtNomor.text.toString()
            alamat = binding.edtAlamat.text.toString()
            password = binding.edtPassword.text.toString()

            updateData(name, email, nomorTelp, alamat, password)
        }
    }

    private fun updateData(name: String, email: String, nomorTelp: String, alamat: String, password: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")
        val user = mapOf<String, String>(
            //"uid" to uid,
            "name" to name,
            "email" to email,
            "nomorTelepon" to nomorTelp,
            "alamat" to alamat,
            "password" to password
        )

        databaseReference.child(uid).updateChildren(user).addOnSuccessListener {
            binding.edtNama.text?.clear()
            binding.edtEmail.text?.clear()
            binding.edtNomor.text?.clear()
            binding.edtAlamat.text?.clear()
            binding.edtPassword.text?.clear()
            Toast.makeText(this, "Berhasil Diubah", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal Diubah", Toast.LENGTH_SHORT).show()
        }
    }
}