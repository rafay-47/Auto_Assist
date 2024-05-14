package com.project.auto_assist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class WorkshopDetail : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_detail_page)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val workShopJson = intent.getStringExtra("workShopJson")
        val gson = Gson()
        val workShop = gson.fromJson(workShopJson, Workshop::class.java)

        setDetails(workShop)

        findViewById<Button>(R.id.btnRequestInquiry).setOnClickListener {
            if(auth.currentUser != null) {
                val intent = Intent(this, ServiceRequestDetails::class.java)
                intent.putExtra("workShopID", workShop?.Id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show()
            }

        }

        findViewById<TextView>(R.id.message).setOnClickListener {
            val intent = Intent(this, ChatBox::class.java)
            intent.putExtra("receiverid", workShop?.Id)
            startActivity(intent)
        }

    }

    private fun setDetails(workShop:Workshop) {
        findViewById<TextView>(R.id.name).setText(workShop.Username)
        findViewById<TextView>(R.id.title).setText(workShop.Username)
        findViewById<TextView>(R.id.location).setText(workShop.Address)
        findViewById<TextView>(R.id.etDescription).setText(workShop.Description)
        findViewById<TextView>(R.id.number).setText(workShop.MobileNo)
        findViewById<TextView>(R.id.email).setText(workShop.Email)
        val imageView = findViewById<ImageView>(R.id.image)
        Picasso.get().load(workShop.ImageUrl).into(imageView)

    }
}
