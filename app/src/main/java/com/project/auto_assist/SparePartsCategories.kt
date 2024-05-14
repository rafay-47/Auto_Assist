package com.project.auto_assist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SparePartsCategories : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spareparts)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        BottomNavBar()

        val Categorie = mutableListOf<CategoryItem>()

        Categorie.add(CategoryItem(R.drawable.img_240_f_392269139, "Body Parts"))
        Categorie.add(CategoryItem(R.drawable.img_engine_500x500_1, "Engine Parts"))
        Categorie.add(CategoryItem(R.drawable.img_suspension_and_brakes_v_2, "Mechanical Parts"))
        Categorie.add(CategoryItem(R.drawable.img_headlights_1, "Electrical Parts"))
        Categorie.add(CategoryItem(R.drawable.img_car_interior_decoration_ideas, "Interior Parts"))
        Categorie.add(CategoryItem(R.drawable.img_94fe3400f7ef6d5, "Exterior Parts"))
        Categorie.add(CategoryItem(R.drawable.img_images_3_1, "Transmission Parts"))
        Categorie.add(CategoryItem(R.drawable.img_27094_d8736b476, "A/C Systems Parts"))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerColumn)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ItemAdapter(Categorie)

        findViewById<ImageView>(R.id.btnOrders).setOnClickListener {
            startActivity(Intent(this, OrdersList::class.java))
        }

    }

    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar)
        bottomNavigationView.selectedItemId = R.id.navigation_spare_parts
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_workshop -> {
                    startActivity(Intent(this, ListWorkshops::class.java))
                    true
                }
                R.id.navigation_request -> {
                    startActivity(Intent(this, ServicesMenu::class.java))
                    true
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this,Home::class.java))
                    true
                }
                R.id.navigation_chats -> {
                    // Handle Chats item click
                    true
                }
                else -> false
            }
        }
    }

}