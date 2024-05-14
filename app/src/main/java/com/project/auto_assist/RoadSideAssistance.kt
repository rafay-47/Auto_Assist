package com.project.auto_assist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class RoadSideAssistance : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var imageURL:String
    private lateinit var imageView: ImageView
    private lateinit var LogoFrame: FrameLayout
    private lateinit var serviceId: String
    private lateinit var geocoder: Geocoder
    var ImageUploaded = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            LogoFrame.setPadding(0, 0, 0, 0)
            imageView.setImageURI(selectedImageUri)


            selectedImageUri?.let { uri ->
                serviceId = db.collection("RoadSideAssistance").document().id
                val storageRef = FirebaseStorage.getInstance().reference.child("RoadSideAssistance/images/${serviceId}")
                storageRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        imageURL = uri.toString()
                        ImageUploaded = true
                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road_side_assistance)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        imageView = findViewById(R.id.imageView)
        LogoFrame = findViewById(R.id.frameStackdslrcamera)
        geocoder = Geocoder(this, Locale.getDefault()) // Initialize Geocoder


        findViewById<Button>(R.id.btnAddress).setOnClickListener {

            if (isGooglePlayServicesAvailable()) {
                checkLocationEnabled()
                requestLocationPermissions()
            } else {
                Toast.makeText(this, "Google Play Services not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        LogoFrame.setOnClickListener {
            openGallery()
        }

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            submitRequest()
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
        }


    }
    private fun submitRequest() {
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val mobile = findViewById<EditText>(R.id.etMobileNo).text.toString()
        val CarBrand = findViewById<EditText>(R.id.etBrand).text.toString()
        val CarModel = findViewById<EditText>(R.id.etModel).text.toString()
        val CarVariant = findViewById<EditText>(R.id.etVariant).text.toString()
        val description = findViewById<EditText>(R.id.etDescription).text.toString()
        val serviceDetail = findViewById<EditText>(R.id.etService).text.toString()
        val location = findViewById<TextView>(R.id.txtAddress1).text.toString()
        val userId = auth.currentUser?.uid.toString()
        if(ImageUploaded) {
            val imageUri = imageURL
            val service = RoadSideAssistanceObject(
                serviceId,
                userId,
                name,
                mobile,
                location,
                CarBrand,
                CarModel,
                CarVariant,
                serviceDetail,
                description,
                imageUri
            )
            db.collection("RoadSideAssistance").document(serviceId).set(service)
                .addOnSuccessListener {
                    Toast.makeText(this, "Request Submitted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
    private fun checkLocationEnabled() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isLocationEnabled) {
            // Prompt user to enable location services
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }
    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == ConnectionResult.SUCCESS
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Location accuracy
    }

    private fun updateLocationUI(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        // Get address using reverse geocoding
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressString = with(StringBuilder()) {
                append(address.getAddressLine(0)) // Street address
                append("\n")
                append(address.getAddressLine(1)) // City, State, ZIP
                toString()
            }
            findViewById<TextView>(R.id.txtAddress1).text = addressString
        } else {
            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show()
        }
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                updateLocationUI(location)
            }
        }
    }
    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private var REQUEST_LOCATION_PERMISSION = 1
    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            requestLocationUpdates()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
