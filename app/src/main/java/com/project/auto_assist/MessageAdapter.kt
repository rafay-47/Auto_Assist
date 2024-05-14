package com.project.auto_assist

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


data class Message(
    var messageid: String? = null,
    var message: String? = null,
    var messageType: String? = null,
    var senderid: String? = null,
    var receiverid: String? = null
)
data class Chat(
    var Senderid: String? = null,
    var Receiverid: String? = null
)
class MessageAdapter(
    val context: Context,
    val messageList: ArrayList<Message>,
    val messageClickListener: MessageClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.image_send, parent, false)
                ImageViewHolder(view)
            }
            2 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.message_send, parent, false)
                MessageViewHolder(view)
            }
            3 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.image_send, parent, false)
                VideoViewHolder(view)
            }
            4 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.message_send, parent, false)
                FileViewHolder(view)
            }
            5 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.image_receive, parent, false)
                ImageViewHolder(view)
            }
            6 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.message_receive, parent, false)
                MessageViewHolder(view)
            }
            7 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.image_send, parent, false)
                VideoViewHolder(view)
            }
            8 -> {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.message_send, parent, false)
                FileViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        when (holder.itemViewType) {
            1 -> {
                val viewHolder = holder as ImageViewHolder
                Picasso.get().load(currentMessage.message).into(viewHolder.sentImage)
                viewHolder.sentImage.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentImage, currentMessage)
                }
            }
            2 -> {
                val viewHolder = holder as MessageViewHolder
                viewHolder.sentMessage.text = currentMessage.message
                viewHolder.sentMessage.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentMessage, currentMessage)
                }
            }
            3 -> {
                val viewHolder = holder as VideoViewHolder
                currentMessage.message?.let { videoUrl ->
                    val videoUri = Uri.parse(videoUrl)
                    viewHolder.sentVideoView.setVideoURI(videoUri)
                    viewHolder.sentVideoView.setOnClickListener {
                        messageClickListener.onMessageClick(viewHolder.sentVideoView, currentMessage)
                    }
                }
            }
            4 -> {
                val viewHolder = holder as FileViewHolder
                viewHolder.sentFile.text = currentMessage.message
                viewHolder.sentFile.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentFile, currentMessage)
                }
            }
            5 -> {
                val viewHolder = holder as ImageViewHolder
                Picasso.get().load(currentMessage.message).into(viewHolder.sentImage)
                viewHolder.sentImage.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentImage, currentMessage)
                }
            }
            6 -> {
                val viewHolder = holder as MessageViewHolder
                viewHolder.sentMessage.text = currentMessage.message
                viewHolder.sentMessage.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentMessage, currentMessage)
                }
            }
            7 -> {
                val viewHolder = holder as VideoViewHolder
                currentMessage.message?.let { videoUrl ->
                    val videoUri = Uri.parse(videoUrl)
                    viewHolder.sentVideoView.setVideoURI(videoUri)
                    viewHolder.sentVideoView.setOnClickListener {
                        messageClickListener.onMessageClick(viewHolder.sentVideoView, currentMessage)
                    }
                }
            }
            8 -> {
                val viewHolder = holder as FileViewHolder
                viewHolder.sentFile.text = currentMessage.message
                viewHolder.sentFile.setOnClickListener {
                    messageClickListener.onMessageClick(viewHolder.sentFile, currentMessage)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messageList[position].senderid == FirebaseAuth.getInstance().currentUser?.uid) {
        return when (messageList[position].messageType) {
            "image" -> 1
            "text" -> 2
            "video" -> 3
            "file" -> 4
            else -> throw IllegalArgumentException("Invalid message type")
        }
        } else {
                return when (messageList[position].messageType) {
                    "image" -> 5
                    "text" -> 6
                    "video" -> 7
                    "file" -> 8
                    else -> throw IllegalArgumentException("Invalid message type")
                }
            }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sentmessage)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentImage = itemView.findViewById<ImageView>(R.id.imageSend)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentVideoView = itemView.findViewById<VideoView>(R.id.imageSend)
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentFile = itemView.findViewById<TextView>(R.id.sentmessage)
    }

    interface MessageClickListener {
        fun onMessageClick(view: View, message: Message)
    }
}
