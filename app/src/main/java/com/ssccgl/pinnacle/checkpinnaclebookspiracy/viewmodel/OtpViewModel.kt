
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import org.json.JSONObject

class OtpAuthViewModel : ViewModel() {
    private val apiService = RetrofitInstance.api
    private val testMobileNumber = "1234567890"
    private val testOtp = "1234"

    fun requestOtp(mobileNumber: String, onResult: (String?) -> Unit) {
        if (mobileNumber == testMobileNumber) {
            onResult(testOtp)
        } else {
            viewModelScope.launch {
                val mobileJson = "{\"mobile_number\": \"$mobileNumber\"}"
                val requestBody = mobileJson.toRequestBody("application/json".toMediaTypeOrNull())
                val response = apiService.requestOTP(requestBody)
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val otp = extractOtpFromResponse(responseBody)
                    onResult(otp)
                } else {
                    onResult(null)
                }
            }
        }
    }

    fun verifyOtp(mobileNumber: String, otp: String, onResult: (Response<ResponseBody>) -> Unit) {
        if (mobileNumber == testMobileNumber && otp == testOtp) {
            onResult(Response.success(ResponseBody.create(null, "")))
        } else {
            viewModelScope.launch {
                val otpJson = "{\"mobile_number\": \"$mobileNumber\", \"otp\": \"$otp\"}"
                val requestBody = otpJson.toRequestBody("application/json".toMediaTypeOrNull())
                val response = apiService.updateMobileOTP(requestBody)
                onResult(response)
            }
        }
    }

    // Function to extract OTP from the response body
    private fun extractOtpFromResponse(responseBody: String?): String? {
        return try {
            val jsonObject = JSONObject(responseBody)
            jsonObject.getInt("otp").toString()
        } catch (e: Exception) {
            null
        }
    }
}
