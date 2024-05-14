package com.project.auto_assist


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso




class OrdersListAdapter(private var cartItems: List<CartItem>) :
    RecyclerView.Adapter<OrdersListAdapter.OrdersListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_list_item, parent, false)
        return OrdersListViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersListViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
    fun updateData(newList: List<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }
    inner class OrdersListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageSparePart)
        private val name: TextView = itemView.findViewById(R.id.txtDftwaterpump)
        private val price: TextView = itemView.findViewById(R.id.txtPrice1)
        private val quantity: TextView = itemView.findViewById(R.id.txtOneOne)
        private val removeButton: Button = itemView.findViewById(R.id.btnRemove)
        private val total: TextView = itemView.findViewById(R.id.txtTotal)

        fun bind(cartItem: CartItem) {
            name.text = cartItem.Name
            price.text = cartItem.Price
            quantity.text = cartItem.Quantity.toString()
            total.text = (cartItem.Quantity * cartItem.Price.toInt()).toString()

            Picasso.get().load(cartItem.ImageUrl).into(image)

            removeButton.setOnClickListener {
                val intent = Intent(itemView.context, ChatBox::class.java)
                intent.putExtra("receiverid", cartItem.SupplierId)
                itemView.context.startActivity(intent)

            }

        }
    }


}
