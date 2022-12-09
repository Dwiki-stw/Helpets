package id.med.helpets.ui.login

import android.app.Activity
import android.app.FragmentManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.databinding.ActivityLoginBinding
import id.med.helpets.ui.home.HomeFragment
import id.med.helpets.ui.main.MainActivity
import id.med.helpets.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.tvRegis.setOnClickListener {
            intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.edtEmail.text!!.isEmpty() || binding.edtPassword.text!!.isEmpty()) {
                Toast.makeText(this, "Isi data terlebih dahulu !", Toast.LENGTH_SHORT).show()
            } else {
                login()
            }
        }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?){
        showLoading(true)
        if (currentUser != null){
            showLoading(false)
            Toast.makeText(this, "Login Berhasil !", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(){
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Login Berhasil !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(this, "Login Gagal !", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(state: Boolean){
        if (state == true){
            binding.progresLogin.visibility = View.VISIBLE
        } else {
            binding.progresLogin.visibility = View.GONE
        }
    }

    companion object{
        private const val TAG = "LoginActivity"
    }
}