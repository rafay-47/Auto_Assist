package com.project.auto_assist

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class EditSparePart : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var imageURL:String
    private lateinit var imageView2: ImageView
    private lateinit var LogoFrame: FrameLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var Id: String
    private lateinit var partId: String
    var ImageUploaded = true

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            ImageUploaded = false
            val selectedImageUri: Uri? = result.data?.data

            imageView2.setImageURI(selectedImageUri)
            LogoFrame.setPadding(0, 0, 0, 0)


            selectedImageUri?.let { uri ->
                Id = auth.currentUser?.uid.toString()
                val storageRef = FirebaseStorage.getInstance().reference.child("SpareParts/images/${Id}")
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
        setContentView(R.layout.edit_spare_part)

        imageView2 = findViewById(R.id.imageDslrcameraone)
        LogoFrame = findViewById(R.id.frameStackdslrcamera)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setDetails()
        LogoFrame.setOnClickListener {
            openGallery()
            LogoFrame.setPadding(0,0,0,0)
        }
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            saveDetails()
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setDetails() {
        val sparePartJson = intent.getStringExtra("sparePartJson")
        if (sparePartJson != null) {
            val gson = Gson()
            val sparePart = gson.fromJson(sparePartJson, SparePart::class.java)
            partId = sparePart.Id
            findViewById<EditText>(R.id.etName).setText(sparePart.Name)
            findViewById<EditText>(R.id.etPrice).setText(sparePart.Price)
            findViewById<EditText>(R.id.etDescription).setText(sparePart.Description)
            imageURL = sparePart.ImageURL
            Picasso.get().load(sparePart.ImageURL).into(imageView2)
        }
    }

    private fun saveDetails() {
        Id = auth.currentUser?.uid.toString()
        val Name = findViewById<EditText>(R.id.etName).text.toString()
        val Price = findViewById<EditText>(R.id.etPrice).text.toString()
        val Description = findViewById<EditText>(R.id.etDescription).text.toString()
        val partId = partId
        Toast.makeText(this, partId, Toast.LENGTH_SHORT).show()
        val sparePart = hashMapOf(
            "Id" to partId,
            "SupplierId" to Id,
            "Name" to Name,
            "Price" to Price,
            "Description" to Description,
            "ImageURL" to imageURL
        )
        db = FirebaseFirestore.getInstance()
        if (ImageUploaded) {
            db.collection("SpareParts").document(partId).set(sparePart)
                .addOnSuccessListener {
                    Toast.makeText(this, "Spare Part Edited", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SupplierMenu::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to edit Spare Part", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
}