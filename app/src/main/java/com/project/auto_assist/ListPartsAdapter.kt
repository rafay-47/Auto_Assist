package com.project.auto_assist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ListPartsAdapter(private var originalSpareParts: List<SparePart>,
private val addToCartClickListener: AddToCartClickListener):
    RecyclerView.Adapter<ListPartsAdapter.ManagePartsViewHolder>() {

    private var spareParts: List<SparePart> = originalSpareParts.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagePartsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.parts_cart_item, parent, false)
        return ManagePartsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManagePartsViewHolder, position: Int) {
        val sparePart = spareParts[position]
        holder.bind(sparePart)
    }

    override fun getItemCount(): Int {
        return spareParts.size
    }

    fun updateData(newList: List<SparePart>) {
        spareParts = newList
        notifyDataSetChanged()
    }

    inner class ManagePartsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageSparePart)
        private val Name: TextView = itemView.findViewById(R.id.txtSparepart)
        private val txtPriceText: TextView = itemView.findViewById(R.id.txtPrice)
        private val AddToCart: AppCompatButton = itemView.findViewById(R.id.btnAddToCart)

        fun bind(sparePart: SparePart) {
            Name.text = sparePart.Name
            txtPriceText.text = buildString {
                append("PKR ")
                append(sparePart.Price)
            }
            Picasso.get().load(sparePart.ImageURL).into(image)


            AddToCart.setOnClickListener {
                addToCartClickListener.onAddToCartClicked(sparePart)
            }
        }
    }
}

interface AddToCartClickListener {
    fun onAddToCartClicked(sparePart: SparePart)
}


