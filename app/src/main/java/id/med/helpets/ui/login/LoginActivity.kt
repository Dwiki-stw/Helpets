package id.med.helpets.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.med.helpets.databinding.ActivityLoginBinding
import id.med.helpets.ui.main.MainActivity
import id.med.helpets.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.tvRegis.setOnClickListener {
            intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}