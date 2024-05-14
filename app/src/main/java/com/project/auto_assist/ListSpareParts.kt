package com.project.auto_assist

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.RangeSlider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson


class ListSpareParts : AppCompatActivity(),AddToCartClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var originalList = mutableListOf<SparePart>()
    private var cartList = mutableListOf<SparePart>()
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_spare_parts)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recyclerUserprofile)

        BottomNavBar()

        val category = intent.getStringExtra("category")
        getSpareParts(category.toString())

        findViewById<ImageView>(R.id.cart).setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            intent.putExtra("cartList", Gson().toJson(cartList))
            startActivity(intent)
        }
        val searchView = findViewById<SearchView>(R.id.searchViewSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }
        })
        val priceRangeSlider = findViewById<RangeSlider>(R.id.range_slider_price)
        priceRangeSlider.addOnChangeListener { slider, _, _ ->
            val minPrice = slider.values[0]
            val maxPrice = slider.values[1]
            Log.d(TAG, "Price range: $minPrice - $maxPrice")
            findViewById<ImageButton>(R.id.btnFilterOne).setOnClickListener {
                getSparePartsInRange(minPrice, maxPrice, category.toString())
            }

        }
        findViewById<ImageView>(R.id.imageArrowdown).setOnClickListener {
            onBackPressed()
        }

    }

    private fun filterData(query: String?) {
        val filteredList = originalList.filter { item ->
            item.Name.contains(query.orEmpty(), ignoreCase = true)
        }
        (recyclerView.adapter as? ListPartsAdapter)?.updateData(filteredList)
    }


    private fun getSpareParts(Classification: String) {
        val sparePartsList = mutableListOf<SparePart>()
        FirebaseFirestore.getInstance().collection("SpareParts")
            .whereEqualTo("Classification", Classification)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val sparePart = document.toObject(SparePart::class.java)
                    sparePartsList.add(sparePart)
                }
                originalList = sparePartsList
                val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = ListPartsAdapter(sparePartsList, this)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting SpareParts", exception)
            }
    }

    private fun getSparePartsInRange(minPrice: Float, maxPrice: Float, category: String) {
        val filteredList = originalList.filter { sparePart ->
            val partPrice = sparePart.Price.toFloatOrNull()
            partPrice != null && partPrice in minPrice..maxPrice && sparePart.Classification == category
        }
        (recyclerView.adapter as? ListPartsAdapter)?.updateData(filteredList)
    }


    override fun onAddToCartClicked(sparePart: SparePart) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_to_cart_dialog, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)

        val name = dialogView.findViewById<TextView>(R.id.txtSuccess)
        val price = dialogView.findViewById<TextView>(R.id.txtPrice)
        name.setText(sparePart.Name)
        price.setText(sparePart.Price)

        val dialog = builder.create()
        dialog.show()

        val button = dialogView.findViewById<Button>(R.id.btnOk)
        button.setOnClickListener {
            val found = cartList.find { it.equals(sparePart) }
            if (found == null) {
                cartList.add(sparePart)
                Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Item already exists in cart!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }


    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar)
        bottomNavigationView.selectedItemId = R.id.navigation_spare_parts
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, Home::class.java))
                    finish()
                    true
                }
                R.id.navigation_request -> {
                    startActivity(Intent(this, ServicesMenu::class.java))
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
