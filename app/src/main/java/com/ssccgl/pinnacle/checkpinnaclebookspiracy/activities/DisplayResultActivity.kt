
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityDisplayResultBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.SearchBarr1Request
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.SearchBarr1Response
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.UpdateStudentBooksAndCoinsRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.DisplayResultViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class DisplayResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayResultBinding

    private val viewModel: DisplayResultViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scannedBarcode = intent.getStringExtra("scannedBarcode")
        val email = intent.getStringExtra("email")
        val resultMessage = intent.getStringExtra("resultMessage")

        if (resultMessage != null) {
            viewModel.handleResultMessage(resultMessage, scannedBarcode, email)
        } else {
            binding.barcodeValue.text = scannedBarcode ?: "No barcode value received"
        }

        binding.buttonScanAgain.setOnClickListener {
            val intent = Intent(this, BarcodeScanActivity::class.java)
            startActivity(intent)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.resultMessage.observe(this) { message ->
            binding.barcodeValue.text = message
        }

        viewModel.barcodeInfo.observe(this) { info ->
            viewModel.searchBarr1(SearchBarr1Request(info.key))
        }

        viewModel.searchBarr1Response.observe(this) { response ->
            showSearchBarr1Response(response)
        }

        viewModel.searchBarcodeResult.observe(this) { result ->
            handleSearchBarcodeResponse(result)
        }

        viewModel.updateBooksAndCoinsResponse.observe(this) {
            displayMessage("50 Coins updated in your account successfully")
        }

        viewModel.errorMessage.observe(this) { message ->
            displayError(message)
        }
    }

    private fun showSearchBarr1Response(response: SearchBarr1Response) {
        val result = response.result
        val message = if (result == "Not Verified") {
            "This book does not belong to us, so it may be doubtful. Contact the publisher at support@ssccglpinnacle.com"
        } else {
            "$result "
        }
        binding.barr1ResultTextView.text = message
        if (result != "Not Verified") {
            val scannedBarcode = intent.getStringExtra("scannedBarcode")
            if (scannedBarcode != null) {
                viewModel.searchBarcode(scannedBarcode)
            }
        }
    }

    private fun handleSearchBarcodeResponse(result: String) {
        when (result) {
            "Not Found" -> {
                val scannedBarcode = intent.getStringExtra("scannedBarcode")
                val email = intent.getStringExtra("email")
                if (scannedBarcode != null && email != null) {
                    viewModel.updateStudentBooksAndCoins(email, UpdateStudentBooksAndCoinsRequest(scannedBarcode))
                }
            }
            "Already Exist" -> displayError("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
            else -> displayError("Unexpected response")
        }
    }

    private fun displayMessage(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = TextView.VISIBLE
    }

    private fun displayError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = TextView.VISIBLE
    }
}
