package com.project.auto_assist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

// WorkshopAdapter.kt
class WorkshopAdapter(private var workshops: List<Workshop>) : RecyclerView.Adapter<WorkshopAdapter.WorkshopViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile, parent, false)
        return WorkshopViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        val workshop = workshops[position]
        holder.bind(workshop)
    }

    override fun getItemCount(): Int {
        return workshops.size
    }
    fun updateData(newList: List<Workshop>) {
        workshops = newList
        notifyDataSetChanged()
    }

    class WorkshopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(workshop: Workshop) {
            itemView.findViewById<TextView>(R.id.txtWorkshopName).text = workshop.Username
            itemView.findViewById<TextView>(R.id.txtLocationText).text = workshop.Address
            val imageView = itemView.findViewById<ImageView>(R.id.imageWorkshopImage)
            Glide.with(itemView).load(workshop.ImageUrl).into(imageView)

            itemView.findViewById<LinearLayout>(R.id.Layout).setOnClickListener {
                val auth:FirebaseAuth = FirebaseAuth.getInstance()
                if(auth.currentUser == null){
                    Toast.makeText(itemView.context, "Please login to view workshop details", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(itemView.context, WorkshopDetail::class.java)
                    val gson = Gson()
                    val workShopJson = gson.toJson(workshop)
                    intent.putExtra("workShopJson", workShopJson)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
