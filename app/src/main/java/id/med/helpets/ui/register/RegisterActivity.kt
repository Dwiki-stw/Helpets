package id.med.helpets.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.databinding.ActivityRegisterBinding
import id.med.helpets.ui.login.LoginActivity
import id.med.helpets.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var name = ""
    private var email = ""
    private var nomorTelp = ""
    private var alamat = ""
    private var password = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            name = binding.edtNama.text.toString().trim()
            email = binding.edtEmail.text.toString().trim()
            nomorTelp = binding.edtNomor.text.toString().trim()
            alamat = binding.edtAlamat.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()
            Register(email, password)
        }

    }

    private fun Register(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(baseContext, "Berhasil", Toast.LENGTH_SHORT).show()
                    updateUI()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI()
                }
            }
    }
//
//    private fun updateUI(currentUser: FirebaseUser?){
//        if (currentUser != null){
//            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//            finish()
//        }
//    }

    private fun updateUI(){
        val timestamp = System.currentTimeMillis()
        val uid = auth.uid
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
            }
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }


}