package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BarcodeViewModel : ViewModel() {

    private val _scannedBarcode = MutableLiveData<String>()
    val scannedBarcode: LiveData<String> get() = _scannedBarcode

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setScannedBarcode(barcode: String) {
        _scannedBarcode.value = barcode
    }
}
