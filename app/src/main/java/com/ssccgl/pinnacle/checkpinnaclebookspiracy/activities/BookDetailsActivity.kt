package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.R
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityBookDetailsBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.BookPurchased
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.UpdateBooksRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.BookDetailsViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private val viewModel: BookDetailsViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }
    private var scannedBarcode: String? = null
    private var email: String? = null
    private var mobileNumber: String? = null
    private var selectedOnlineMode: String = ""
    private var purchaseType: Int = R.id.radioButtonOnline

    companion object {
        private const val STATE_SCANNED_BARCODE = "scannedBarcode"
        private const val STATE_EMAIL = "email"
        private const val STATE_MOBILE_NUMBER = "mobileNumber"
        private const val STATE_SELECTED_ONLINE_MODE = "selectedOnlineMode"
        private const val STATE_PURCHASE_TYPE = "purchaseType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Book Details"

        // Restore state
        scannedBarcode = intent.getStringExtra("scannedBarcode") ?: savedInstanceState?.getString(STATE_SCANNED_BARCODE)
        email = intent.getStringExtra("email") ?: savedInstanceState?.getString(STATE_EMAIL)
        mobileNumber = intent.getStringExtra("mobileNumber") ?: savedInstanceState?.getString(STATE_MOBILE_NUMBER)
        selectedOnlineMode = savedInstanceState?.getString(STATE_SELECTED_ONLINE_MODE) ?: ""
        purchaseType = savedInstanceState?.getInt(STATE_PURCHASE_TYPE) ?: R.id.radioButtonOnline

        binding.txtScannedBarcode5.text = scannedBarcode
        //binding.txtScannedBarcode6.text = email
        //binding.txtMobileNumber.text = mobileNumber

        initViews()
        setupListeners()
        observeViewModel()

        // Set the saved purchase type and update the UI
        binding.radioGroupPurchaseType.check(purchaseType)
        updatePurchaseTypeVisibility(purchaseType)

        // Set the saved spinner position
        val onlineModePosition = (binding.spinnerOnlineMode.adapter as? AdapterView<*>)?.let {
            (0 until it.count).firstOrNull { pos ->
                it.getItemAtPosition(pos).toString() == selectedOnlineMode
            } ?: 0
        } ?: 0
        binding.spinnerOnlineMode.setSelection(onlineModePosition)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SCANNED_BARCODE, scannedBarcode)
        outState.putString(STATE_EMAIL, email)
        outState.putString(STATE_MOBILE_NUMBER, mobileNumber)
        outState.putString(STATE_SELECTED_ONLINE_MODE, selectedOnlineMode)
        outState.putInt(STATE_PURCHASE_TYPE, binding.radioGroupPurchaseType.checkedRadioButtonId)
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

    private fun initViews() {
        binding.spinnerOnlineMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOnlineMode = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedOnlineMode = ""
            }
        }
    }

    private fun setupListeners() {
        binding.radioGroupPurchaseType.setOnCheckedChangeListener { _, checkedId ->
            updatePurchaseTypeVisibility(checkedId)
        }

        binding.buttonSave.setOnClickListener {
            saveData()
        }
    }

    private fun updatePurchaseTypeVisibility(checkedId: Int) {
        when (checkedId) {
            R.id.radioButtonOnline -> {
                binding.onlineLayout.visibility = View.VISIBLE
                binding.localBookshopLayout.visibility = View.GONE
            }
            R.id.radioButtonLocalBookshop -> {
                binding.onlineLayout.visibility = View.GONE
                binding.localBookshopLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun saveData() {
        val searchBarcode = scannedBarcode ?: ""

        if (email.isNullOrEmpty()) {
            showToast("Email cannot be empty")
            return
        }

        val booksPurchased: List<BookPurchased>

        when (binding.radioGroupPurchaseType.checkedRadioButtonId) {
            R.id.radioButtonOnline -> {
                val orderNumber = binding.editTextOrderNumber.text.toString()
                val seller = binding.editTextSellerName.text.toString()

                if (orderNumber.isEmpty() || seller.isEmpty() || selectedOnlineMode.isEmpty()) {
                    showToast("Order number, seller, and online mode cannot be empty")
                    return
                }

                booksPurchased = listOf(
                    BookPurchased(
                        searchBarcode = searchBarcode,
                        pincode = "",
                        city = "",
                        OnlineMode = selectedOnlineMode,
                        OrderNum = orderNumber,
                        OnlineSeller = seller,
                        BookShop = "",
                        BookshopAddress = ""
                    )
                )
            }
            R.id.radioButtonLocalBookshop -> {
                val pinCode = binding.editTextPinCode.text.toString()
                if (pinCode.length != 6 || pinCode.toIntOrNull() == null) {
                    showToast("Enter valid Pincode")
                    return
                }

                val city = binding.editTextPlace.text.toString()
                val bookShop = binding.editTextBookshopName.text.toString()
                val bookshopAddress = binding.editTextBookshopAddress.text.toString()

                if (pinCode.isEmpty() || city.isEmpty() || bookShop.isEmpty() || bookshopAddress.isEmpty()) {
                    showToast("Pin code, city, bookshop name, and bookshop address cannot be empty")
                    return
                }

                booksPurchased = listOf(
                    BookPurchased(
                        searchBarcode = searchBarcode,
                        pincode = pinCode,
                        city = city,
                        OnlineMode = "",
                        OrderNum = "",
                        OnlineSeller = "",
                        BookShop = bookShop,
                        BookshopAddress = bookshopAddress
                    )
                )
            }
            else -> {
                showToast("Please select a purchase type")
                return
            }
        }

        val request = UpdateBooksRequest(Books_Purchased = booksPurchased)
        viewModel.updateStudentBooks(email!!, request)
    }

    private fun observeViewModel() {
        viewModel.updateBooksResponse.observe(this) { response ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            if (response.isSuccessful) {
                val jsonResponse = response.body()?.string() ?: ""
                val prettyJson = gson.toJson(gson.fromJson(jsonResponse, Any::class.java))
                val intent = Intent(this@BookDetailsActivity, DisplayResultActivity::class.java).apply {
                    putExtra("resultMessage", prettyJson)
                    putExtra("scannedBarcode", scannedBarcode)
                    putExtra("email", email)
                }
                startActivity(intent)
            } else {
                val errorResponse = response.errorBody()?.string() ?: "Failed to update books"
                val prettyErrorJson = gson.toJson(gson.fromJson(errorResponse, Any::class.java))
                val intent = Intent(this@BookDetailsActivity, DisplayResultActivity::class.java).apply {
                    putExtra("resultMessage", prettyErrorJson)
                    putExtra("scannedBarcode", scannedBarcode)
                    putExtra("email", email)
                }
                startActivity(intent)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
