package com.ssccgl.pinnacle.checkpinnaclebookspiracy.model

data class LoginResponse(
    val status: String,
    val mobileNumber: String?,
    val details: List<Any>?
)
