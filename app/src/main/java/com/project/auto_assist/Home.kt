package com.project.auto_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class Home : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var profilePic: ImageView
    private lateinit var signIn: TextView
    private lateinit var userType: String

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("tttt", token)
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show()


            })
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("tt", token)
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()


            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        profilePic = findViewById(R.id.profilePic)
        signIn = findViewById(R.id.signIn)
        askNotificationPermission()
        Toast.makeText(this, auth.currentUser?.uid.toString(), Toast.LENGTH_SHORT).show()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("tttt", token)
            Toast.makeText(this, token, Toast.LENGTH_SHORT).show()


        })

        if(auth.currentUser != null) {
            signIn.visibility = View.GONE
            getUserData()
        } else {
            profilePic.visibility = View.INVISIBLE
            signIn.setOnClickListener {
                startActivity(Intent(this, Login::class.java))
            }
        }
        ViewPager()
        getWorkshops()
        getSpareParts()
        BottomNavBar()

        findViewById<ImageView>(R.id.imageOutputOne).setOnClickListener{
            drawerLayout.openDrawer(navigationView)
            SideDrawer()
        }

    }

    fun ViewPager(){
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val images = listOf(
            R.drawable.img_banner_2_1,
            R.drawable.img_banner_2_1,
            R.drawable.img_banner_2_1
        )
        val adapter = InfinitePagerAdapter(this, images)
        viewPager.adapter = adapter

        val middlePosition = Integer.MAX_VALUE / 2
        adapter.setCurrentItem(middlePosition)
    }

    fun SideDrawer(){

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation item clicks here
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    auth = FirebaseAuth.getInstance()
                    if (auth.currentUser != null) {
                        startActivity(Intent(this, Profile::class.java))
                    } else {
                        Toast.makeText(this, "Please login to access profile", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_register -> {
                    startActivity(Intent(this, Register::class.java))
                    true
                }
                R.id.nav_login -> {
                    if(auth.currentUser?.uid == null)
                        startActivity(Intent(this, Login::class.java))
                    else
                        Toast.makeText(this,"Already Logged In", Toast.LENGTH_SHORT)
                    true
                }
                R.id.nav_register_workshop -> {
                    startActivity(Intent(this, WorkshopRegistration::class.java))
                    true
                }
                R.id.nav_register_supplier -> {
                    startActivity(Intent(this, SupplierRegistration::class.java))
                    true
                }
                R.id.nav_login_workshop -> {
                    auth = FirebaseAuth.getInstance()
                    if(auth.currentUser != null && userType == "Workshop"){
                        startActivity(Intent(this, WorkshopDashboard::class.java))
                    } else {
                        val intent = Intent(this, Login::class.java)
                        intent.putExtra("type", "Workshops")
                        startActivity(intent)
                    }
                    true
                }
                R.id.nav_login_supplier -> {
                    auth = FirebaseAuth.getInstance()
                    if(auth.currentUser != null && userType == "Supplier"){
                        startActivity(Intent(this, SupplierMenu::class.java))
                    } else {
                        val intent = Intent(this, Login::class.java)
                        intent.putExtra("type", "Suppliers")
                        startActivity(intent)
                    }
                    true
                }
                R.id.nav_logout -> {
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    notification()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView)
            } else {
                drawerLayout.openDrawer(navigationView)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
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
                // Initialize RecyclerView and set adapter
                val recyclerView: RecyclerView = findViewById(R.id.WorkshopsrecyclerView)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = WorkshopAdapter(workshops)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

    }

    private fun getSpareParts() {
        val sparePartsList = mutableListOf<SparePart>()
        FirebaseFirestore.getInstance().collection("SpareParts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val sparePart = document.toObject(SparePart::class.java)
                    sparePartsList.add(sparePart)
                }
                val recyclerView: RecyclerView = findViewById(R.id.SparePartsrecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = SparePartAdapter(sparePartsList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting SpareParts", exception)
            }
    }


    private fun BottomNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_app_bar)
        bottomNavigationView.selectedItemId = R.id.navigation_home
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
                R.id.navigation_chats -> {
                    auth = FirebaseAuth.getInstance()
                    if(auth.currentUser != null) {
                        startActivity(Intent(this, ChatsList::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Please login to access chats", Toast.LENGTH_SHORT).show()
                    }
                        true

                }
                else -> false
            }
        }
    }

    private fun getUserData() {
        db.collection("Users")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(User::class.java)
                    if (userData != null) {
                        signIn.text = userData.Username
                        if (userData.ImageUrl != "") {
                            Glide.with(this)
                                .load(userData.ImageUrl)
                                .into(profilePic)
                        }
                        userType = userData.UserType
                    }
                } else {
                    Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
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
            .setSmallIcon(R.drawable.img_nav_workshop)
            .setContentTitle("Logged Out!")
            .setContentText("Visit again! We have amazing things for you.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }
}
