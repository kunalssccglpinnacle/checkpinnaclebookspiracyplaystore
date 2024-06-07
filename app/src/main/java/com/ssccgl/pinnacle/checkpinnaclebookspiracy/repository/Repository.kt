package com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository

import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.*
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.ApiService
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class Repository(private val apiService: ApiService) {

    suspend fun verifyEmail(request: EmailVerificationRequest): Response<Void> {
        return apiService.verifyEmail(request)
    }

    suspend fun updateStudentBooks(email: String, request: UpdateBooksRequest): Response<ResponseBody> {
        return apiService.updateStudentBooks(email, request)
    }

    suspend fun loginWithEmailAndPassword(request: LoginRequest): Response<LoginResponse> {
        return apiService.loginWithEmailAndPassword(request)
    }

    suspend fun getKey(barcode: String): Response<GetKeyResponse> {
        return apiService.getKey(barcode)
    }

    suspend fun searchBarr1(request: SearchBarr1Request): Response<SearchBarr1Response> {
        return apiService.searchBarr1(request)
    }

    suspend fun searchBarcode(barcode: String): Response<String> {
        return apiService.searchBarcode(barcode)
    }

    suspend fun updateStudentBooksAndCoins(email: String, request: UpdateStudentBooksAndCoinsRequest): Response<UpdateStudentBooksAndCoinsResponse> {
        return apiService.updateStudentBooksAndCoins(email, request)
    }

    suspend fun sendForgotPasswordEmail(request: ForgotPasswordRequest): Response<ForgotPasswordResponse> {
        return apiService.sendForgotPasswordEmail(request)
    }
}
