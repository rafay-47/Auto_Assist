package com.project.auto_assist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsListAdapter(private val context: Context, private var userList: List<User>) :
    RecyclerView.Adapter<ChatsListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_layout_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    fun updateData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val imageView: CircleImageView = itemView.findViewById(R.id.image)
        private val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)

        fun bind(user: User) {
            nameTextView.text = user.Username
            // Load user image using Picasso or any other image loading library
            if(user.ImageUrl == "")
                Picasso.get().load(R.drawable.baseline_person_24).into(imageView)
            else
                Picasso.get().load(user.ImageUrl).into(imageView)
            constraintLayout.setOnClickListener {
                val intent = Intent(context, ChatBox::class.java)
                intent.putExtra("receiverid", user.Id)
                context.startActivity(intent)
            }
        }
    }
}
