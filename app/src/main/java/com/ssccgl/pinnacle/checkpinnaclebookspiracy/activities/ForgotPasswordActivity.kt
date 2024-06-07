package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityForgotPasswordBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.ForgotPasswordRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ForgotPasswordViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Forgot Password"

        binding.buttonSubmitForgotPassword.setOnClickListener {
            val email = binding.editTextEmailForgotPassword.text.toString()
            if (email.isBlank()) {
                showMessage("Please enter your email address")
                return@setOnClickListener
            }
            sendForgotPasswordEmail(email)
        }
    }

    private fun sendForgotPasswordEmail(email: String) {
        val request = ForgotPasswordRequest(email_id = email)
        viewModel.sendForgotPasswordEmail(request).observe(this) { response ->
            if (response.isSuccessful) {
                showMessage("Password reset email sent successfully")
            } else {
                showMessage("Failed to send password reset email")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
}
