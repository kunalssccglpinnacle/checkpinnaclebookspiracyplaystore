package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.UpdateBooksRequest
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class BookDetailsViewModel(private val repository: Repository) : ViewModel() {

    private val _updateBooksResponse = MutableLiveData<Response<ResponseBody>>()
    val updateBooksResponse: LiveData<Response<ResponseBody>> get() = _updateBooksResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun updateStudentBooks(email: String, request: UpdateBooksRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateStudentBooks(email, request)
                _updateBooksResponse.value = response
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to update books: ${e.localizedMessage}")
            }
        }
    }
}
