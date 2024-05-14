package com.project.auto_assist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class Order(
    val orderId: String,
    val userId: String,
    val supplierId: String,
    val phone: String,
    val address: String,
    val Name: String,
    val Quantity: String,
    val ImageURL: String,
    val Amount: String,
    val paymentMethod: String,
) {
    constructor() : this("", "", "","", "", "","","", "", "")
}


class OrdersList : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getOrders()

    }

    private fun getOrders() {
        val ordersList = mutableListOf<CartItem>()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("Orders")
                .whereEqualTo("userId", userId)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        if (order != null) {
                            val item = CartItem(
                                order.orderId,
                                order.userId,
                                order.Name,
                                order.Amount,
                                order.Quantity.toInt(),
                                order.Amount.toInt(),
                                order.ImageURL
                            )
                            ordersList.add(item)
                        }
                    }
                    Toast.makeText(this, ordersList.size.toString(), Toast.LENGTH_SHORT).show()
                    displayOrders(ordersList)
                }
        }
    }


    private fun displayOrders(ordersList: MutableList<CartItem>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val adapter = OrdersListAdapter(ordersList)
        recyclerView.adapter = adapter
    }


}