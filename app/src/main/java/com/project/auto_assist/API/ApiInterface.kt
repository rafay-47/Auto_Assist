package com.app.lawconnect.API

import com.app.lawconnect.Models.CustomerModel
import com.app.lawconnect.Models.PaymentIntentModel
import com.app.lawconnect.utils.SECRET_KEY
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer() : Response<CustomerModel>


    @Headers(
        "Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2023-10-16")
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ) : Response<CustomerModel>


    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: String,
        @Query("currency") currency: String = "pkr",
        @Query("automatic_payment_methods[enabled]") automatePay:Boolean = true
    ) : Response<PaymentIntentModel>

}