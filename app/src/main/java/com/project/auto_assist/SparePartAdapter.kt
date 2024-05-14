package com.project.auto_assist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SparePartAdapter(private val spareParts: List<SparePart>) :
    RecyclerView.Adapter<SparePartAdapter.SparePartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparePartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_userprofile1, parent, false)
        return SparePartViewHolder(view)
    }

    override fun onBindViewHolder(holder: SparePartViewHolder, position: Int) {
        val sparePart = spareParts[position]
        // Bind data to the item view
        holder.bind(sparePart)
    }

    override fun getItemCount(): Int {
        return spareParts.size
    }

    inner class SparePartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSteeringWheelIm: ImageView = itemView.findViewById(R.id.imageSteeringWheelIm)
        private val txtSteeringWheelTe: TextView = itemView.findViewById(R.id.txtSteeringWheelTe)
        private val txtPriceText: TextView = itemView.findViewById(R.id.txtPriceText)

        fun bind(sparePart: SparePart) {
            txtSteeringWheelTe.text = sparePart.Name
            txtPriceText.text = buildString {
                append("PKR ")
                append(sparePart.Price)
            }
            Picasso.get().load(sparePart.ImageURL).into(imageSteeringWheelIm)

        }
    }

}
