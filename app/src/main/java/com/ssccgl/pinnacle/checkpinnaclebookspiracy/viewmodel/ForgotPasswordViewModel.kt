package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.ForgotPasswordRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.ForgotPasswordResponse
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ForgotPasswordViewModel(private val repository: Repository) : ViewModel() {

    fun sendForgotPasswordEmail(request: ForgotPasswordRequest): LiveData<Response<ForgotPasswordResponse>> {
        val responseLiveData = MutableLiveData<Response<ForgotPasswordResponse>>()
        viewModelScope.launch {
            val response = repository.sendForgotPasswordEmail(request)
            responseLiveData.value = response
        }
        return responseLiveData
    }
}
