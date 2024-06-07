package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityStudentDetailsBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.StudentDetailsViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class StudentDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailsBinding
    private val apiService = RetrofitInstance.api
    private val viewModel: StudentDetailsViewModel by viewModels {
        ViewModelFactory(Repository(apiService))
    }

    private var scannedBarcode: String? = null
    private var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Student Details"

        // Get the scanned barcode and mobile number from the Intent
        scannedBarcode = intent.getStringExtra("scannedBarcode")
        mobileNumber = intent.getStringExtra("mobileNumber")

        // Log the values for debugging
        Log.d("StudentDetails", "Scanned Barcode: $scannedBarcode")
        Log.d("StudentDetails", "Mobile Number: $mobileNumber")

        // Display the scanned barcode and mobile number in the TextViews
        binding.txtScannedBarcode1.text = scannedBarcode ?: "No Barcode Scanned"
       // binding.txtScannedBarcode2.text = mobileNumber ?: "No Mobile Number Provided"

        // Set up the submit button click listener
        binding.buttonSubmit.setOnClickListener {
            val fullName = binding.editTextFullName.text.toString()
            val email = binding.editTextEmail.text.toString()

            if (fullName.isBlank() || email.isBlank()) {
                showToast("Please enter both name and email")
                return@setOnClickListener
            }

            mobileNumber?.let { viewModel.verifyEmail(it, fullName, email) }
        }

        // Set up the login TextView click listener
        binding.textViewLogin.setOnClickListener {
            navigateToLogin(mobileNumber, scannedBarcode)
        }

        setupObservers()
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

    private fun setupObservers() {
        viewModel.emailVerificationResult.observe(this, Observer { result ->
            result.onSuccess {
                showDialog1("For Email Verification and create password we have sent a link on Email Please verify before proceed", mobileNumber!!, it.email, it.fullName)
            }.onFailure {
                showDialog("Email already registered, please login")
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            showToast(message)
        })
    }

    private fun navigateToLogin(mobileNumber: String?, scannedBarcode: String?) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("mobileNumber", mobileNumber)
            putExtra("scannedBarcode", scannedBarcode)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDialog1(message: String, mobileNumber: String, email: String, fullName: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
