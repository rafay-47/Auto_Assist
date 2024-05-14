package com.project.auto_assist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServicesMenu : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_menu)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        BottomNavBar()

        findViewById<Button>(R.id.btnRoadAssistance).setOnClickListener {
            startActivity(Intent(this,RoadSideAssistance::class.java))
        }
        findViewById<Button>(R.id.btnBookService).setOnClickListener {
            startActivity(Intent(this,ListWorkshops::class.java))
        }
        findViewById<Button>(R.id.btnAddParts).setOnClickListener {
            startActivity(Intent(this,SparePartsCategories::class.java))
        }
        findViewById<ImageView>(R.id.services).setOnClickListener {
            startActivity(Intent(this,BookingsList::class.java))
        }
    }

    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar)
        bottomNavigationView.selectedItemId = R.id.navigation_request
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_spare_parts -> {
                    startActivity(Intent(this, SparePartsCategories::class.java))
                    finish()
                    true
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this, Home::class.java))
                    finish()
                    true
                }
                R.id.navigation_workshop -> {
                    startActivity(Intent(this,ListWorkshops::class.java))
                    finish()
                    true
                }
                R.id.navigation_chats -> {
                    startActivity(Intent(this, ChatsList::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

}