package com.project.auto_assist

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListWorkshops: AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var originalList = mutableListOf<Workshop>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var Filtertype = "Name"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_workshops)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recyclerUserprofile)
        searchView = findViewById(R.id.searchViewSearch)
        BottomNavBar()
        getWorkshops()
        findViewById<ImageButton>(R.id.btnFilterOne).setOnClickListener{
            onFilterClick(it)
        }

        val searchView = findViewById<SearchView>(R.id.searchViewSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission (optional)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                if (Filtertype == "Name") {
                    filterDataByName(newText)
                } else {
                    filterDataByLocation(newText)
                }
                return true
            }
        })
    }

    private fun filterDataByName(query: String?) {
        val filteredList = originalList.filter { item ->
            item.Username.contains(query.orEmpty(), ignoreCase = true)
        }
        (recyclerView.adapter as? WorkshopAdapter)?.updateData(filteredList)
    }
    private fun filterDataByLocation(query: String?) {
        val filteredList = originalList.filter { item ->
            item.Address.contains(query.orEmpty(), ignoreCase = true)
        }
        (recyclerView.adapter as? WorkshopAdapter)?.updateData(filteredList)
    }

    fun onFilterClick(view: View) {
        val popupMenu = PopupMenu(this, view,R.style.CustomPopupMenuStyle)
        popupMenu.getMenuInflater().inflate(R.menu.search_worshops_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search_by_name -> {
                    searchView.queryHint = "Search by Name"
                    Filtertype = "Name"
                    true
                }
                R.id.search_by_location -> {
                    searchView.queryHint = "Search by Location"
                    Filtertype = "Location"
                    true
                }
                else -> false
            }
        }
        popupMenu.gravity = Gravity.END
        popupMenu.show()

    }

    private fun getWorkshops() {
        val workshops = mutableListOf<Workshop>()

        FirebaseFirestore.getInstance().collection("Users")
            .whereEqualTo("UserType", "Workshop")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val workshop = document.toObject(Workshop::class.java)
                    workshops.add(workshop)
                }
                originalList = workshops.toMutableList()
                val recyclerView: RecyclerView = findViewById(R.id.recyclerUserprofile)
                val layoutManager = GridLayoutManager(this, 2,GridLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = WorkshopAdapter(workshops)
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar)
        bottomNavigationView.selectedItemId = R.id.navigation_workshop
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_spare_parts -> {
                    startActivity(Intent(this, SparePartsCategories::class.java))
                    finish()
                    true
                }
                R.id.navigation_request -> {
                    startActivity(Intent(this, ServicesMenu::class.java))
                    finish()
                    true
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this,Home::class.java))
                    finish()
                    true
                }
                R.id.navigation_chats -> {
                    startActivity(Intent(this,ChatsList::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}