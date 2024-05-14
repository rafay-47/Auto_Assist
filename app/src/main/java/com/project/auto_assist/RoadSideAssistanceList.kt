package com.project.auto_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RoadSideAssistanceList : AppCompatActivity(), RoadSideAssistanceAdapter.OnItemClickListener{
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roadside_requests_list)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        val roadSideAssistanceList = mutableListOf<RoadSideAssistanceObject>()
        db.collection("RoadSideAssistance")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val request = document.toObject(RoadSideAssistanceObject::class.java)
                    roadSideAssistanceList.add(request)
                }
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = RoadSideAssistanceAdapter(this, roadSideAssistanceList, this)

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting SpareParts", exception)
            }

    }
    override fun onItemClick() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1 // Unique ID for the notification

        val channelId = "notification_channel"
        val channelName = "com.project.i212582_i210607"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_repair_service_1)
            .setContentTitle("Road Side Assistance")
            .setContentText("Request Accepted! Mechanic is on the way.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())


    }

}
