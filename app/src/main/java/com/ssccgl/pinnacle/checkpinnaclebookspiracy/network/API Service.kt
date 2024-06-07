package com.ssccgl.pinnacle.checkpinnaclebookspiracy.network

import com.ssccgl.pinnacle.checkpinnaclebookspiracy.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login-with-mobile")
    suspend fun requestOTP(@Body mobileNumber: RequestBody): Response<ResponseBody>

    @POST("update-mobile-otp")
    suspend fun updateMobileOTP(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("email-verification-mobile-fullname")
    suspend fun verifyEmail(@Body request: EmailVerificationRequest): Response<Void>

    @PUT("updateStudentBooks/{email}")
    suspend fun updateStudentBooks(@Path("email") email: String, @Body request: UpdateBooksRequest): Response<ResponseBody>

    @POST("login-with-email-pass")
    suspend fun loginWithEmailAndPassword(@Body request: LoginRequest): Response<LoginResponse>

    @GET("getKey/{barcode}")
    suspend fun getKey(@Path("barcode") barcode: String): Response<GetKeyResponse>

    @POST("searchBarr1")
    suspend fun searchBarr1(@Body request: SearchBarr1Request): Response<SearchBarr1Response>

    @GET("searchBarcode/{barcode}")
    suspend fun searchBarcode(@Path("barcode") barcode: String): Response<String>

    @PUT("updateStudentBooksAndCoins/{email}")
    suspend fun updateStudentBooksAndCoins(@Path("email") email: String, @Body request: UpdateStudentBooksAndCoinsRequest): Response<UpdateStudentBooksAndCoinsResponse>

    @POST("send-Email")
    suspend fun sendForgotPasswordEmail(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
}
