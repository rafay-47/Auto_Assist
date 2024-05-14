package com.project.auto_assist


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class ChatBox : AppCompatActivity(), MessageAdapter.MessageClickListener{

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userdatabase: DatabaseReference
    private lateinit var messageRecyclerView:RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var ReceiverName: TextView
    private lateinit var imageUri: String
    private lateinit var senderid: String
    private lateinit var receiverid: String


    var receiverRoom: String? = null
    var senderRoom: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            imageUri = selectedImageUri.toString()

            selectedImageUri?.let { uri ->
                val uniqueID = UUID.randomUUID().toString()
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("Chats/images/${uniqueID}")
                storageRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageURL = uri.toString()
                        val message = Message(
                            UUID.randomUUID().toString(), imageURL, "image",
                            senderid, receiverid
                        )

                        database.child(senderRoom!!).child("messages").push()
                            .setValue(message).addOnSuccessListener {
                                database.child(receiverRoom!!).child("messages").push()
                                    .setValue(message).addOnSuccessListener {}

                            }.addOnFailureListener { exception ->
                                Log.e("GalleryClass", "Upload failed", exception)
                            }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        auth = FirebaseAuth.getInstance()

        receiverid = intent.getStringExtra("receiverid")!!

        senderid = auth.currentUser?.uid!!

        ReceiverName = findViewById(R.id.txtAutoassist)
        if (ReceiverName.text == "Name")
            setReceiverName(receiverid)

        findViewById<ImageView>(R.id.back).setOnClickListener{
            onBackPressed()
        }


        senderRoom = receiverid + senderid
        receiverRoom = senderid + receiverid
        getChat(senderRoom!!, receiverRoom!!)



        Toast.makeText(this, senderRoom, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, receiverRoom, Toast.LENGTH_SHORT).show()


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Chats")


        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.etThumbsup)
        messageList = ArrayList()

        messageAdapter = MessageAdapter(this,messageList, this)


        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager
        messageRecyclerView.adapter = messageAdapter

        database.child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    // messageAdapter.notifyDataSetChanged()
                    messageAdapter.notifyItemInserted(messageAdapter.itemCount - 1)
                    messageRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                }
                override fun onCancelled(error: DatabaseError) { }

            })



        messageBox.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (messageBox.right - messageBox.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {

                    val message =  Message(UUID.randomUUID().toString(), messageBox.text.toString(), "text",
                                senderid, receiverid)

                    database.child(senderRoom!!).child("messages").push()
                        .setValue(message).addOnSuccessListener {
                            database.child(receiverRoom!!).child("messages").push()
                                .setValue(message).addOnSuccessListener {

                                }
                        }
                    messageBox.setText("")

                    return@setOnTouchListener true
                }
                if (event.rawX <= (messageBox.left + messageBox.compoundDrawables[DRAWABLE_LEFT].bounds.width())) {
                    openGallery()
                    return@setOnTouchListener true
                }
            }
            false
        }

    }

    private fun setReceiverName(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(User::class.java)
                    if (userData != null) {
                        ReceiverName.text = userData.Username
                        findViewById<CircleImageView>(R.id.profilePic1).let {
                            Glide.with(this)
                                .load(userData.ImageUrl)
                                .into(it)
                        }
                    } else {
                        Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    override fun onMessageClick(view: View, message: Message) {
        // Handle message click
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    fun addChat(Senderid: String, Receiverid: String, SenderRoom: String, ReceiverRoom: String) {
        FirebaseFirestore.getInstance().collection("Chats")
            .document(SenderRoom).set(mapOf("Senderid" to Senderid, "Receiverid" to Receiverid))
            .addOnSuccessListener {
                FirebaseFirestore.getInstance().collection("Chats")
                    .document(ReceiverRoom).set(mapOf("Senderid" to Receiverid, "Receiverid" to Senderid))
                    .addOnSuccessListener {
                    }
            }
    }


    fun getChat(SenderRoom: String, ReceiverRoom: String) {
        val Chats = mutableListOf<Chat>()
        FirebaseFirestore.getInstance().collection("Chats")
            .document(SenderRoom).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val chat = document.toObject(Chat::class.java)
                    if (chat != null) {
                        Chats.add(chat)
                    }
                }
                if(Chats.isEmpty()){
                    addChat(senderid, receiverid, senderRoom!!, receiverRoom!!)
                }
            }
            .addOnFailureListener { exception ->
            }
    }
}