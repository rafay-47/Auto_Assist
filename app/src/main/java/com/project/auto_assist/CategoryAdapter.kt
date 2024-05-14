package com.project.auto_assist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

data class CategoryItem(val image: Int, val buttonText: String)

class ItemAdapter(private val itemList: List<CategoryItem>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_column, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView = itemView.findViewById(R.id.image240f392269139)
        private val button: Button = itemView.findViewById(R.id.btnButtonText)

        fun bind(item: CategoryItem) {
            imageView.setImageResource(item.image)
            button.text = item.buttonText
            button.setOnClickListener {
                val intent = Intent(itemView.context, ListSpareParts::class.java)
                intent.putExtra("category", item.buttonText)
                itemView.context.startActivity(intent)
            }
        }
    }
}
