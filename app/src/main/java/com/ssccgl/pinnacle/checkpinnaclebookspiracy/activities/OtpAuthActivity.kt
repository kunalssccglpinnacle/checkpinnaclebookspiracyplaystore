
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.R
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.OtpAuthViewModel

class OtpAuthActivity : AppCompatActivity() {

    private val viewModel: OtpAuthViewModel by viewModels()
    private val testMobileNumber = "1234567890"
    private val testOtp = "1234"
    private var lastScannedBarcode: String? = null
    private var receivedOtp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_auth)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "OTP Authentication"

        val editTextMobile = findViewById<EditText>(R.id.editTextMobile)
        val buttonRequestOTP = findViewById<Button>(R.id.buttonRequestOTP)
        val editTextOTP = findViewById<EditText>(R.id.editTextOTP)
        val buttonVerifyOTP = findViewById<Button>(R.id.buttonVerifyOTP)
        val textViewScannedBarcode = findViewById<TextView>(R.id.txtScannedBarcode)

        // For testing purposes, pre-fill the mobile number and OTP fields
//        editTextMobile.setText(testMobileNumber)
//        editTextOTP.setText(testOtp)

        // Get the scanned barcode from the intent and display it
        lastScannedBarcode = intent.getStringExtra("scannedBarcode")
        textViewScannedBarcode.text = lastScannedBarcode

        buttonRequestOTP.setOnClickListener {
            val mobileNumber = editTextMobile.text.toString().trim()
            if (mobileNumber.isEmpty() || mobileNumber.length != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.requestOtp(mobileNumber) { otp ->
                runOnUiThread {
                    if (otp != null) {
                        receivedOtp = otp
                        Toast.makeText(this, "OTP requested successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to request OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonVerifyOTP.setOnClickListener {
            val mobileNumber = editTextMobile.text.toString().trim()
            val otp = editTextOTP.text.toString().trim()
            if (mobileNumber.isEmpty() || mobileNumber.length != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (otp.isEmpty() || otp.length != 4) {
                Toast.makeText(this, "Please enter a valid 4-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mobileNumber == testMobileNumber && otp == testOtp) {
                // Directly proceed for the test mobile number and OTP
                navigateToStudentDetails(mobileNumber)
            } else {
                if (otp == receivedOtp) {
                    viewModel.verifyOtp(mobileNumber, otp) { response ->
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show()
                                navigateToStudentDetails(mobileNumber)
                            } else {
                                Toast.makeText(this, "Failed to verify OTP", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid OTP entered", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToStudentDetails(mobileNumber: String) {
        // Navigate to StudentDetailsActivity
        val intent = Intent(this, StudentDetailsActivity::class.java).apply {
            putExtra("scannedBarcode", lastScannedBarcode)
            putExtra("mobileNumber", mobileNumber)
        }
        startActivity(intent)
        finish()
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
