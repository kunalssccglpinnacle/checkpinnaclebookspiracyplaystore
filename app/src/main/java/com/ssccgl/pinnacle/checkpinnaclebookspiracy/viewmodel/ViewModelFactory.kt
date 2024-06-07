package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BarcodeViewModel::class.java) -> {
                BarcodeViewModel() as T
            }
            modelClass.isAssignableFrom(OtpAuthViewModel::class.java) -> {
                OtpAuthViewModel() as T
            }
            modelClass.isAssignableFrom(StudentDetailsViewModel::class.java) -> {
                StudentDetailsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DisplayResultViewModel::class.java) -> {
                DisplayResultViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BookDetailsViewModel::class.java) -> {
                BookDetailsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java) -> {
                ForgotPasswordViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
