package com.project.auto_assist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChatsList : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private var originalList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_list)
        BottomNavBar()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.chatsRecyclerView)

        getChats()


        val searchView = findViewById<SearchView>(R.id.searchViewSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission (optional)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                filterData(newText)
                return true
            }
        })


    }
    private fun filterData(query: String?) {
        val filteredList = originalList.filter { item ->
            item.Username.contains(query.orEmpty(), ignoreCase = true)
        }
        (recyclerView.adapter as? ChatsListAdapter)?.updateData(filteredList)
    }

    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar1)
        bottomNavigationView.selectedItemId = R.id.navigation_chats
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
                R.id.navigation_workshop -> {
                    startActivity(Intent(this,ListWorkshops::class.java))
                    finish()
                    true
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this, Home::class.java))
                    finish()
                    true
                }
                R.id.navigation_chats -> return@setOnNavigationItemSelectedListener true
                else -> false
            }
        }
    }

    private fun getChats() {
        val UsersList = mutableListOf<User>()
        val user = auth.currentUser
        db.collection("Chats")
            .whereEqualTo("Senderid", user?.uid)
            .get().addOnSuccessListener { documents ->
            for (document in documents) {
                val chat = document.toObject(Chat::class.java)
                if(chat.Receiverid != null) {
                    db.collection("Users")
                        .document(chat.Receiverid!!)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userData = document.toObject(User::class.java)
                                if (userData != null) {
                                    UsersList.add(userData)
                                }
                            }
                            Toast.makeText(this, UsersList.size.toString(),Toast.LENGTH_SHORT).show()
                            originalList = UsersList
                            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            val adapter = ChatsListAdapter(this, originalList)
                            recyclerView.adapter = adapter
                        }
                        .addOnFailureListener { exception ->
                        }
                }
            }
        }.addOnFailureListener{}
    }

}