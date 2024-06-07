package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.EmailVerificationRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EmailVerificationResult(val mobileNumber: String, val email: String, val fullName: String)

class StudentDetailsViewModel(private val repository: Repository) : ViewModel() {

    private val _emailVerificationResult = MutableLiveData<Result<EmailVerificationResult>>()
    val emailVerificationResult: LiveData<Result<EmailVerificationResult>> get() = _emailVerificationResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun verifyEmail(mobileNumber: String, fullName: String, email: String) {
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            _errorMessage.postValue("Please enter a valid email address")
            return
        }
        viewModelScope.launch {
            try {
                val request = EmailVerificationRequest(mobileNumber, email, fullName, "")
                val response = repository.verifyEmail(request)
                if (response.isSuccessful) {
                    _emailVerificationResult.postValue(Result.success(EmailVerificationResult(mobileNumber, email, fullName)))
                } else {
                    _emailVerificationResult.postValue(Result.failure(HttpException(response)))
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to verify, please try again: ${e.message}")
            }
        }
    }
}
