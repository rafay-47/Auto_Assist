package com.project.auto_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        findViewById<TextView>(R.id.txtLogin).setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val Username = findViewById<EditText>(R.id.username).text.toString()
            val Password = findViewById<EditText>(R.id.etPassword).text.toString()
            val Email = findViewById<EditText>(R.id.etEmail).text.toString()


            auth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val Id = auth.currentUser?.uid.toString()
                        val user = hashMapOf(
                            "Id" to Id,
                            "Username" to Username,
                            "Password" to Password,
                            "Email" to Email,
                            "ImageUrl" to "",
                            "UserType" to "User"
                        )
                        db.collection("Users").document(Id).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                notification()
                                startActivity(Intent(this,Home::class.java))
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
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
            .setSmallIcon(R.drawable.baseline_person_add_alt_1_24)
            .setContentTitle("Registration Successful")
            .setContentText("Congrats! You have successfully registered.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }
}
