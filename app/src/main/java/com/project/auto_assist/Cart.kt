package com.project.auto_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.lawconnect.API.ApiInterface
import com.app.lawconnect.API.ApiUtilities
import com.app.lawconnect.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Cart : AppCompatActivity(), CartItemAdapter.OnRemoveItemClickListener,
    CartItemAdapter.OnPlusClickListener, CartItemAdapter.OnMinusItemClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartItemsList: List<CartItem>
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var TotalAmount: String
    private lateinit var selectedPaymentMethod: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        val cartListJson = intent.getStringExtra("cartList")

        val gson = Gson()
        val SparePartListType = object : TypeToken<List<SparePart>>() {}.type
        val partsList: List<SparePart> = gson.fromJson(cartListJson, SparePartListType)
        Toast.makeText(this, partsList.size.toString(), Toast.LENGTH_SHORT).show()

        val cartList = partsList.map { sparePart ->
            CartItem(
                sparePart.Id,
                sparePart.SupplierId,
                sparePart.Name,
                sparePart.Price,
                1,
                sparePart.Price.toInt(),
                sparePart.ImageURL
            )
        }
        cartItemsList = cartList


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCart)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = CartItemAdapter(cartList, this,this,this)
        recyclerView.adapter = adapter

        updatePrices(cartList)

        PaymentConfiguration.init(this, utils.PUBLISHABLE_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        findViewById<Button>(R.id.btnBuyNow).setOnClickListener {
            if(auth.currentUser?.uid != null) {
                if (selectedPaymentMethod == "Cash on Delivery") {
                    addOrderToDatabase("Cash on Delivery")
                } else if (selectedPaymentMethod == "Credit/Debit Card") {
                    paymentFlow(TotalAmount.toDouble())
                }
            } else {
                Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }
        getPaymentMethod()

    }

    private fun getPaymentMethod() {
        val radioGroup = findViewById<RadioGroup>(R.id.paymentMethodRadioGroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)

            selectedPaymentMethod = selectedRadioButton.text.toString()

            if (selectedPaymentMethod == "Cash on Delivery") {
                findViewById<Button>(R.id.btnBuyNow).text = "Place Order"
            } else {
                findViewById<Button>(R.id.btnBuyNow).text = "Pay Now"
            }
        }

    }
    override fun onRemoveItemClicked(position: Int) {
        val cartItems = cartItemsList.toMutableList()
        cartItems.removeAt(position)
        cartItemsList = cartItems
        val adapter = findViewById<RecyclerView>(R.id.recyclerViewCart).adapter as CartItemAdapter
        adapter.updateData(cartItems)
        updatePrices(cartItems)
    }
    override fun onAddItemClicked(cartItems: List<CartItem>) {
        updatePrices(cartItems)
    }
    override fun onMinusItemClicked(cartItems: List<CartItem>) {
        updatePrices(cartItems)
    }

    private fun updatePrices(cartItems: List<CartItem>) {
        val SubTotal = cartItems.sumBy { it.TotalPrice }.toString()
        findViewById<TextView>(R.id.txtTotalPrice).text = SubTotal
        val discount = findViewById<TextView>(R.id.txtDiscountPrice).text.toString().toInt()
        TotalAmount = (SubTotal.toInt() - discount.toString().toInt()).toString()
        findViewById<TextView>(R.id.txtTotalPrice00).text = TotalAmount
    }


    private var apiInterface: ApiInterface = ApiUtilities.getApiInterface()
    private fun getCustomerId(callback: (String) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = apiInterface.getCustomer()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!.id)
                } else {
                    // Handle error
                }
            }
        }
    }

    private fun getEphemeralKey(customerId: String, callback: (String) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = apiInterface.getEphemeralKey(customerId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!.id)
                } else {
                    // Handle error
                }
            }
        }
    }

    private fun getPaymentIntent(customerId: String,fees: Double, callback: (String) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = apiInterface.getPaymentIntent(customerId,fees.toInt().toString()+"00")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!.client_secret)
                } else {
                    // Handle error
                }
            }
        }
    }

    private fun paymentFlow(fees:Double) {
        Toast.makeText(this, "Please Wait!", Toast.LENGTH_LONG).show()
        getCustomerId { customerId ->
            getEphemeralKey(customerId) { ephemeralKey ->
                getPaymentIntent(customerId,fees) { clientSecret ->
                    paymentSheet.presentWithPaymentIntent(
                        clientSecret,
                        PaymentSheet.Configuration(
                            "LawConnect",
                            PaymentSheet.CustomerConfiguration(
                                customerId, ephemeralKey
                            )
                        )
                    )
                }
            }
        }
    }
    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if(paymentSheetResult is PaymentSheetResult.Completed){
            notification()
            addOrderToDatabase("Credit/Debit Card")
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Home::class.java))
        }
    }

    private fun notification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val channelId = "notification_channel"
        val channelName = "com.project.i212582_i210607"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_nav_spare_parts)
            .setContentTitle("Order Placed!")
            .setContentText("Your order has been placed successfully. Items on the way.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }

    private fun addOrderToDatabase(paymentMethod: String) {

        for (item in cartItemsList) {
            val orderId = db.collection("Orders").document().id
            val phone = findViewById<EditText>(R.id.etPhoneNo).text.toString()
            val address = findViewById<EditText>(R.id.etAddress).text.toString()
            val order = hashMapOf(
                "orderId" to orderId,
                "userId" to auth.currentUser?.uid,
                "supplierId" to item.SupplierId,
                "phone" to phone,
                "address" to address,
                "Name" to item.Name,
                "Quantity" to item.Quantity.toString(),
                "ImageURL" to item.ImageUrl,
                "Amount" to item.Price,
                "paymentMethod" to paymentMethod
            )

            db.collection("Orders").document(orderId)
                .set(order)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Home::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error placing order", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
