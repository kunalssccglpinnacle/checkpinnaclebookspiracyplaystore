package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityLoginBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.LoginRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.LoginViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }

    private var scannedBarcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Login"

        // Get the scanned barcode from the Intent
        scannedBarcode = intent.getStringExtra("scannedBarcode")
        binding.txtScannedBarcode.text = scannedBarcode

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                showToast("Please enter both email and password")
                return@setOnClickListener
            }
            loginWithEmailAndPassword(email, password)
        }

        binding.textForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loginWithEmailAndPassword(email: String, password: String) {
        val request = LoginRequest(email, password)
        viewModel.loginWithEmailAndPassword(request).observe(this) { response ->
            if (response.isSuccessful && response.body()?.status == "success") {
                val mobileNumber = response.body()?.mobileNumber
                showToast("Login successful")
                val intent = Intent(this@LoginActivity, BookDetailsActivity::class.java).apply {
                    putExtra("email", email) // Pass email to BookDetailsActivity
                    putExtra("scannedBarcode", scannedBarcode)
                    putExtra("mobileNumber", mobileNumber) // Pass mobile number to BookDetailsActivity
                }
                startActivity(intent)
            } else {
                showToast("Login failed: Invalid email or password")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
