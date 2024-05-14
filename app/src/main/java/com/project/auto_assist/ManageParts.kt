package com.project.auto_assist

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ManageParts : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_parts)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        getSpareParts(auth.currentUser?.uid.toString())

        findViewById<Button>(R.id.btnAddSpareParts).setOnClickListener {
            startActivity(Intent(this, AddSparePart::class.java))
        }

    }


    private fun getSpareParts(supplierId: String) {
        val sparePartsList = mutableListOf<SparePart>()
        FirebaseFirestore.getInstance().collection("SpareParts")
            .whereEqualTo("SupplierId", supplierId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val sparePart = document.toObject(SparePart::class.java)
                    sparePartsList.add(sparePart)
                }
                val recyclerView: RecyclerView = findViewById(R.id.ManagePartsRecyclerView)
                val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = ManagePartsAdapter(sparePartsList)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting SpareParts", exception)
            }
    }

}