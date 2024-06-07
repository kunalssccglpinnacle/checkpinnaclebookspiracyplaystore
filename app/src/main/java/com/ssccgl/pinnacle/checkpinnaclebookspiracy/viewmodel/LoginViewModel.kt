package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.LoginRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.LoginResponse
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun loginWithEmailAndPassword(request: LoginRequest): LiveData<Response<LoginResponse>> {
        val responseLiveData = MutableLiveData<Response<LoginResponse>>()
        viewModelScope.launch {
            val response = repository.loginWithEmailAndPassword(request)
            responseLiveData.value = response
        }
        return responseLiveData
    }
}
