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
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class AddSparePart : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var imageURL:String
    private lateinit var imageView2: ImageView
    private lateinit var LogoFrame: FrameLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var Id: String
    private lateinit var partId: String
    var ImageUploaded = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data

            imageView2.setImageURI(selectedImageUri)

            selectedImageUri?.let { uri ->
                partId = db.collection("SpareParts").document().id
                val storageRef = FirebaseStorage.getInstance().reference.child("SpareParts/images/${partId}")
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
        setContentView(R.layout.add_spare_part)

        imageView2 = findViewById(R.id.imageDslrcameraone)
        LogoFrame = findViewById(R.id.frameStackdslrcamera)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //spinner()

        LogoFrame.setOnClickListener {
            openGallery()
            LogoFrame.setPadding(0,0,0,0)
        }
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            addSparePart()
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            onBackPressed()
        }
    }


    private fun addSparePart() {
        val Name = findViewById<EditText>(R.id.etName).text.toString()
        val Price = findViewById<EditText>(R.id.etPrice).text.toString()
        val Description = findViewById<EditText>(R.id.etDescription).text.toString()
        val Classification = findViewById<Spinner>(R.id.spinner).selectedItem.toString()
        Id = auth.currentUser?.uid.toString()
        Toast.makeText(this, partId, Toast.LENGTH_SHORT).show()

        db = FirebaseFirestore.getInstance()

        if (ImageUploaded) {
            val sparePart = hashMapOf(
                "Id" to partId,
                "SupplierId" to Id,
                "Name" to Name,
                "Price" to Price,
                "Description" to Description,
                "ImageURL" to imageURL,
                "Classification" to Classification
            )
            db.collection("SpareParts").document(partId).set(sparePart)
                .addOnSuccessListener {
                    Toast.makeText(this, "Spare Part Added", Toast.LENGTH_SHORT).show()
                    notification()
                    startActivity(Intent(this, SupplierMenu::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add Spare Part", Toast.LENGTH_SHORT).show()
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
            .setSmallIcon(R.drawable.img_nav_spare_parts)
            .setContentTitle("Added Spare Part!")
            .setContentText("Spare Part Added!. You can view spare parts at Spare Parts Menu.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }

}