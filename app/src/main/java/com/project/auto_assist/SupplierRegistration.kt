package com.project.auto_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SupplierRegistration : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var uid:String
    private lateinit var profilePicUri:String
    private lateinit var imageURL:String
    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var BannerFrame: FrameLayout
    private lateinit var LogoFrame: FrameLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var Id: String
    var ImageUploaded = false
    var flag = 0

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            if (flag == 1) {
                imageView2.setImageURI(selectedImageUri)
                LogoFrame.setPadding(0, 0, 0, 0)
            } else {
                imageView.setImageURI(selectedImageUri)
                BannerFrame.setPadding(0, 0, 0, 0)
            }

            selectedImageUri?.let { uri ->
                Id = auth.currentUser?.uid.toString()
                val storageRef = FirebaseStorage.getInstance().reference.child("Suppliers/images/${Id}")
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
        setContentView(R.layout.activity_register_supplier)

        imageView = findViewById(R.id.imageDslrcameraone1)
        imageView2 = findViewById(R.id.imageDslrcameraone)
        BannerFrame = findViewById(R.id.frameStackdslrcamera1)
        LogoFrame = findViewById(R.id.frameStackdslrcamera)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        LogoFrame.setOnClickListener {
            flag = 1
            openGallery()
        }
        BannerFrame.setOnClickListener {
            flag = 2
            openGallery()
        }
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            RegisterSupplier()
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
        }
    }

    private fun RegisterSupplier() {
        val Email = findViewById<EditText>(R.id.etEmail).text.toString()
        val Password = findViewById<EditText>(R.id.etPassword).text.toString()
        auth.createUserWithEmailAndPassword(Email, Password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addSupplier()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addSupplier() {
        val Email = findViewById<EditText>(R.id.etEmail).text.toString()
        val Name = findViewById<EditText>(R.id.etName).text.toString()
        val Description = findViewById<EditText>(R.id.etDescription).text.toString()
        val MobileNo = findViewById<EditText>(R.id.etMobileNo).text.toString()
        val Address = findViewById<EditText>(R.id.etAddress).text.toString()
        Id = auth.currentUser?.uid.toString()

        val workshop = hashMapOf(
            "Id" to Id,
            "Username" to Name,
            "Email" to Email,
            "Description" to Description,
            "MobileNo" to MobileNo,
            "Address" to Address,
            "ImageUrl" to imageURL,
            "UserType" to "Supplier"
        )
        if (ImageUploaded) {
            db.collection("Users").document(Id).set(workshop)
                .addOnSuccessListener {
                    Toast.makeText(this, "Supplier Added", Toast.LENGTH_SHORT).show()
                    notification()
                    startActivity(Intent(this, SupplierMenu::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add Supplier", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun notification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val channelId = "notification_channel"
        val channelName = "com.project.i212582_i210607"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_person_add_alt_1_24)
            .setContentTitle("Registration Successful")
            .setContentText("Congrats! You have successfully registered.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }
}