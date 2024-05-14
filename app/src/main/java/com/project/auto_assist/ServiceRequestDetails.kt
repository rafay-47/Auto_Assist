package com.project.auto_assist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ServiceRequestDetails : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var imageURL:String
    private lateinit var imageView: ImageView
    private lateinit var LogoFrame: FrameLayout
    private lateinit var serviceId: String
    var ImageUploaded = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            LogoFrame.setPadding(0, 0, 0, 0)
            imageView.setImageURI(selectedImageUri)


            selectedImageUri?.let { uri ->
                serviceId = UUID.randomUUID().toString()
                val storageRef = FirebaseStorage.getInstance().reference.child("BookedServices/images/${serviceId}")
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
        setContentView(R.layout.activity_service_request_details)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        imageView = findViewById(R.id.imageView)
        LogoFrame = findViewById(R.id.frameStackdslrcamera)

        val workShopID = intent.getStringExtra("workShopID")

        LogoFrame.setOnClickListener {
            openGallery()
        }

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            submitRequest(workShopID!!)
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
        }
    }
    private fun submitRequest(workShopID: String) {
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val mobile = findViewById<EditText>(R.id.etMobileNo).text.toString()
        val CarBrand = findViewById<EditText>(R.id.etBrand).text.toString()
        val CarModel = findViewById<EditText>(R.id.etModel).text.toString()
        val CarVariant = findViewById<EditText>(R.id.etVariant).text.toString()
        val description = findViewById<EditText>(R.id.etDescription).text.toString()
        val serviceDetail = findViewById<EditText>(R.id.etService).text.toString()
        val imageUri = imageURL
        val userId = auth.currentUser?.uid.toString()
        val service = Service(
            userId,
            workShopID,
            name,
            mobile,
            CarBrand,
            CarModel,
            CarVariant,
            serviceDetail,
            description,
            imageUri
        )
        db.collection("BookedServices").add(service)
            .addOnSuccessListener {
                Toast.makeText(this, "Request Submitted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
}
