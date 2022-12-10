package id.med.helpets.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.databinding.ActivityRegisterBinding
import id.med.helpets.dataclass.User
import id.med.helpets.ui.login.LoginActivity
import id.med.helpets.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
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

        binding.tvRegis.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            name = binding.edtNama.text.toString()
            email = binding.edtEmail.text.toString()
            nomorTelp = binding.edtNomor.text.toString()
            alamat = binding.edtAlamat.text.toString()
            password = binding.edtPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || nomorTelp.isEmpty() || alamat.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Lengkapi data diri anda terlebih dahulu !", Toast.LENGTH_LONG).show()
            } else {
                Register(email, password)
            }
        }

    }

    private fun Register(email: String, password: String){
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    showLoading(false)
                    Log.d(TAG, "signInWithEmail:success")
                    updateUI()
                } else {
                    showLoading(false)
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    updateUI()
                }
            }
    }

    private fun updateUI(){
        val uid = auth.currentUser?.uid
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["nomorTelepon"] = nomorTelp
        hashMap["alamat"] = alamat
        hashMap["password"] = password

        databaseReference = FirebaseDatabase.getInstance().getReference("DataUser")

        val user = User(uid, name, email, nomorTelp, alamat, password)
        if (uid != null){
            showLoading(true)
            databaseReference.child(uid).setValue(user)
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        showLoading(false)
                        Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        showLoading(false)
                        Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun showLoading(state: Boolean){
        if (state == true){
            binding.progresRegister.visibility = View.VISIBLE
        } else {
            binding.progresRegister.visibility = View.GONE
        }
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }
}