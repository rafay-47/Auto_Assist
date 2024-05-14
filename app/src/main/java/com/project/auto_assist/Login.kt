package com.project.auto_assist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
  private lateinit var db: FirebaseFirestore
  private lateinit var auth: FirebaseAuth
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    db = FirebaseFirestore.getInstance()
    auth = FirebaseAuth.getInstance()

    val type = intent.getStringExtra("type")?: "User"

    findViewById<TextView>(R.id.txtRegister).setOnClickListener {
      startActivity(Intent(this,Register::class.java))
      finish()
    }

    findViewById<Button>(R.id.btnLogIn).setOnClickListener {
      val Password = findViewById<EditText>(R.id.etPassword).text.toString()
      val Email = findViewById<EditText>(R.id.etEmail).text.toString()

      auth.signInWithEmailAndPassword(Email, Password)
        .addOnCompleteListener(this) { task ->
          if (task.isSuccessful) {
            getUserData(type) { user ->
              auth = FirebaseAuth.getInstance()
              if (user?.Id != "" && auth.currentUser?.uid == user?.Id) {
                if (user?.UserType == "Supplier")
                  startActivity((Intent(this, Home::class.java)))
                else if (user?.UserType == "Workshop")
                  startActivity(Intent(this, Home::class.java))
                else if(user?.UserType == "User")
                  startActivity(Intent(this, Home::class.java))

                Toast.makeText(this, "Login Successful " + auth.currentUser.toString(), Toast.LENGTH_SHORT).show()
              }
            }
          }
        }.addOnFailureListener {
          Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
  }

  private fun getUserData(type: String, callback: (User?) -> Unit) {
    db.collection("Users")
      .document(auth.currentUser?.uid.toString())
      .get()
      .addOnSuccessListener { document ->
        if (document != null) {
          val userData = document.toObject(User::class.java)
          if (userData != null) {
            callback(userData)
          } else {
            callback(null)
            Toast.makeText(this, "No $type Found!", Toast.LENGTH_SHORT).show()
          }
        } else {
          callback(null)
          Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
        }
      }
      .addOnFailureListener { exception ->
        callback(null)
      }
  }



}
