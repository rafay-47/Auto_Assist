package com.project.auto_assist

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkshopBookings : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_bookings_list)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        findViewById<ImageButton>(R.id.back).setOnClickListener {
            onBackPressed()
        }


        val ServicesList = mutableListOf<Service>()
        db.collection("BookedServices")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val request = document.toObject(Service::class.java)
                    ServicesList.add(request)
                }
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = BookingsListAdapter(this, ServicesList)

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting SpareParts", exception)
            }

    }

}
