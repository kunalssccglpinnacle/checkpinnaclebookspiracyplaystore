package com.ssccgl.pinnacle.checkpinnaclebookspiracy.model

data class EmailVerificationRequest(
    val mobile_number: String,
    val email_id: String,
    val full_name: String,
    val lastScannedBarcode: String
)
