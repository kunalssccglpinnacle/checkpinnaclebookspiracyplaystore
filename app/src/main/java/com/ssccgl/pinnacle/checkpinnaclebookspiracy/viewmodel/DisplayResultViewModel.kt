//package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.gson.JsonParser
//import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.*
//import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
//import kotlinx.coroutines.launch
//
//class DisplayResultViewModel(private val repository: Repository) : ViewModel() {
//
//    private val _resultMessage = MutableLiveData<String>()
//    val resultMessage: LiveData<String> get() = _resultMessage
//
//    private val _barcodeInfo = MutableLiveData<GetKeyResponse>()
//    val barcodeInfo: LiveData<GetKeyResponse> get() = _barcodeInfo
//
//    private val _searchBarr1Response = MutableLiveData<SearchBarr1Response>()
//    val searchBarr1Response: LiveData<SearchBarr1Response> get() = _searchBarr1Response
//
//    private val _searchBarcodeResult = MutableLiveData<String>()
//    val searchBarcodeResult: LiveData<String> get() = _searchBarcodeResult
//
//    private val _updateBooksAndCoinsResponse = MutableLiveData<UpdateStudentBooksAndCoinsResponse>()
//    val updateBooksAndCoinsResponse: LiveData<UpdateStudentBooksAndCoinsResponse> get() = _updateBooksAndCoinsResponse
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> get() = _errorMessage
//
//    fun handleResultMessage(resultMessage: String, scannedBarcode: String?, email: String?) {
//        viewModelScope.launch {
//            try {
//                val jsonElement = JsonParser.parseString(resultMessage)
//                if (jsonElement.isJsonObject) {
//                    val jsonObject = jsonElement.asJsonObject
//                    if (jsonObject.has("error") && jsonObject.get("error").asString == "Duplicate Entry") {
//                        _errorMessage.postValue("Duplicate barcode: $scannedBarcode contact the publisher support@ssccglpinnacle.com")
//                    } else {
//                        _resultMessage.postValue("Barcode: $scannedBarcode")
//                        scannedBarcode?.let { getBarcodeInfo(it) }
//                    }
//                } else {
//                    _errorMessage.postValue("Invalid response format")
//                }
//            } catch (e: Exception) {
//                _errorMessage.postValue("Error parsing result message: ${e.localizedMessage}")
//            }
//        }
//    }
//
//    private fun getBarcodeInfo(barcode: String) {
//        viewModelScope.launch {
//            try {
//                val response = repository.getKey(barcode)
//                if (response.isSuccessful) {
//                    response.body()?.let { _barcodeInfo.postValue(it) }
//                } else {
//                    _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//                }
//            } catch (e: Exception) {
//                _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//            }
//        }
//    }
//
//    fun searchBarr1(request: SearchBarr1Request) {
//        viewModelScope.launch {
//            try {
//                val response = repository.searchBarr1(request)
//                if (response.isSuccessful) {
//                    response.body()?.let { _searchBarr1Response.postValue(it) }
//                } else {
//                    _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//                }
//            } catch (e: Exception) {
//                _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//            }
//        }
//    }
//
//    fun searchBarcode(barcode: String) {
//        viewModelScope.launch {
//            try {
//                val response = repository.searchBarcode(barcode)
//                if (response.isSuccessful) {
//                    response.body()?.let { _searchBarcodeResult.postValue(it) }
//                } else {
//                    _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//                }
//            } catch (e: Exception) {
//                _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//            }
//        }
//    }
//
//    fun updateStudentBooksAndCoins(email: String, request: UpdateStudentBooksAndCoinsRequest) {
//        viewModelScope.launch {
//            try {
//                val response = repository.updateStudentBooksAndCoins(email, request)
//                if (response.isSuccessful) {
//                    response.body()?.let { _updateBooksAndCoinsResponse.postValue(it) }
//                } else {
//                    _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//                }
//            } catch (e: Exception) {
//                _errorMessage.postValue("This book may be doubtful, contact the publisher support@ssccglpinnacle.com")
//            }
//        }
//    }
//}
//
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.*
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance.api
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstanceNodei
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import kotlinx.coroutines.launch

class DisplayResultViewModel(private val repository: Repository) : ViewModel() {

    private val _resultMessage = MutableLiveData<String>()
    val resultMessage: LiveData<String> get() = _resultMessage

    private val _barcodeInfo = MutableLiveData<GetKeyResponse>()
    val barcodeInfo: LiveData<GetKeyResponse> get() = _barcodeInfo

    private val _searchBarr1Response = MutableLiveData<SearchBarr1Response>()
    val searchBarr1Response: LiveData<SearchBarr1Response> get() = _searchBarr1Response

    private val _searchBarcodeResult = MutableLiveData<String>()
    val searchBarcodeResult: LiveData<String> get() = _searchBarcodeResult

    private val _updateBooksAndCoinsResponse = MutableLiveData<UpdateStudentBooksAndCoinsResponse>()
    val updateBooksAndCoinsResponse: LiveData<UpdateStudentBooksAndCoinsResponse> get() = _updateBooksAndCoinsResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun handleResultMessage(resultMessage: String, scannedBarcode: String?, email: String?) {
        viewModelScope.launch {
            try {
                val jsonElement = JsonParser.parseString(resultMessage)
                if (jsonElement.isJsonObject) {
                    val jsonObject = jsonElement.asJsonObject
                    if (jsonObject.has("error") && jsonObject.get("error").asString == "Duplicate Entry") {
                        _errorMessage.postValue("Duplicate barcode: $scannedBarcode contact the publisher support@ssccglpinnacle.com")
                    } else {
                        _resultMessage.postValue("Barcode: $scannedBarcode")
                        scannedBarcode?.let { getBarcodeInfo(it) }
                    }
                } else {
                    _errorMessage.postValue("Invalid response format")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error parsing result message: ${e.localizedMessage}")
            }
        }
    }

    private fun showBarcodeInfo(barcodeInfo: GetKeyResponse) {
        _barcodeInfo.postValue(barcodeInfo)
    }

    private fun displayMessage(message: String) {
        _errorMessage.postValue(message)
    }

    private fun displayError(message: String) {
        _errorMessage.postValue(message)
    }

    fun getBarcodeInfo(barcode: String) {
        if (barcode.contains("-")) {
            searchBarr1(SearchBarr1Request(barcode))
        } else {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstanceNodei.api.getKey(barcode)
                    if (response.isSuccessful) {
                        response.body()?.let { showBarcodeInfo(it) } ?: displayError("Failed to retrieve key from the response")
                    } else {
                        displayError("Barcode info retrieval failed: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    displayError("Error fetching barcode info: ${e.localizedMessage}")
                }
            }
        }
    }

    fun searchBarr1(request: SearchBarr1Request) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstanceNodei.api.searchBarr1(request)
                if (response.isSuccessful) {
                    response.body()?.let { _searchBarr1Response.postValue(it) } ?: displayError("Failed to retrieve search Barr1 response")
                } else {
                    displayError("Search Barr1 failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                displayError("Error during search Barr1: ${e.localizedMessage}")
            }
        }
    }

    fun searchBarcode(barcode: String) {
        viewModelScope.launch {
            try {
                val response = repository.searchBarcode(barcode)
                if (response.isSuccessful) {
                    response.body()?.let { _searchBarcodeResult.postValue(it) } ?: displayError("Failed to retrieve search barcode result")
                } else {
                    displayError("Search barcode failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                displayError("Error during barcode search: ${e.localizedMessage}")
            }
        }
    }

    fun updateStudentBooksAndCoins(email: String, request: UpdateStudentBooksAndCoinsRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateStudentBooksAndCoins(email, request)
                if (response.isSuccessful) {
                    response.body()?.let { _updateBooksAndCoinsResponse.postValue(it) } ?: displayError("Failed to update student books and coins")
                } else {
                    displayError("Update student books and coins failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                displayError("Error updating student books and coins: ${e.localizedMessage}")
            }
        }
    }
}
