package com.ssccgl.pinnacle.checkpinnaclebookspiracy.model

data class BookPurchased(
    val searchBarcode: String,
    val pincode: String,
    val city: String,
    val OnlineMode: String,
    val OrderNum: String,
    val OnlineSeller: String,
    val BookShop: String,
    val BookshopAddress: String
)
