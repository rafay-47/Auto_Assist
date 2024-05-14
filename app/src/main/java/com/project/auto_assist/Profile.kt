package com.project.auto_assist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class Profile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var profilePic: ImageView
    private lateinit var Id: String
    private lateinit var imageURL:String
    private lateinit var password: String
    var ImageUploaded = true

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data

            profilePic.setImageURI(selectedImageUri)

            selectedImageUri?.let { uri ->
                Id = auth.currentUser?.uid.toString()
                val storageRef = FirebaseStorage.getInstance().reference.child("Users/images/${Id}")
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
        setContentView(R.layout.activity_view_profile)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        profilePic = findViewById(R.id.profilePic)

        getUserData()

        profilePic.setOnClickListener {
            ImageUploaded = false
            openGallery()
        }
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            if(ImageUploaded)
                saveUserData()
            else
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageView>(R.id.imageArrowdownOne).setOnClickListener {
            onBackPressed()
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
                        findViewById<EditText>(R.id.etName).setText(userData.Username)
                        findViewById<EditText>(R.id.etEmail).setText(userData.Email)
                        if(userData.ImageUrl != "")
                            Picasso.get().load(userData.ImageUrl).into(profilePic)
                        password = userData.Password
                    }
                } else {
                    Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun saveUserData() {
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val email = findViewById<EditText>(R.id.etEmail).text.toString()

        Id = auth.currentUser?.uid.toString()
        val userDataUpdates = hashMapOf<String, Any>(
            "Id" to Id,
            "Username" to name,
            "Email" to email,
            "ImageUrl" to imageURL,
        )
        Toast.makeText(this,name + email ,Toast.LENGTH_SHORT).show()
        // Update the document
        db.collection("Users").document(Id)
            .update(userDataUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

}
