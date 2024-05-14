package com.project.auto_assist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

data class CartItem(
    val Id: String,
    val SupplierId: String,
    val Name: String,
    val Price: String,
    var Quantity: Int,
    var TotalPrice: Int,
    val ImageUrl: String
)


class CartItemAdapter(private var cartItems: List<CartItem>, private val listener: OnRemoveItemClickListener
                      , private val listener1: OnPlusClickListener, private val listener2: OnMinusItemClickListener) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
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
    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageSparePart)
        private val name: TextView = itemView.findViewById(R.id.txtDftwaterpump)
        private val price: TextView = itemView.findViewById(R.id.txtPrice1)
        private val quantity: TextView = itemView.findViewById(R.id.txtOneOne)
        private val removeButton: Button = itemView.findViewById(R.id.btnRemove)
        private val plusButton: ImageButton = itemView.findViewById(R.id.btnEditplusOne)
        private val minusButton: ImageButton = itemView.findViewById(R.id.btnEditminusOne)
        private val total: TextView = itemView.findViewById(R.id.txtTotal)

        fun bind(cartItem: CartItem) {
            name.text = cartItem.Name
            price.text = cartItem.Price
            quantity.text = cartItem.Quantity.toString()
            total.text = (cartItem.Quantity * cartItem.Price.toInt()).toString()

            Picasso.get().load(cartItem.ImageUrl).into(image)

            removeButton.setOnClickListener {
                listener.onRemoveItemClicked(adapterPosition)
            }
            plusButton.setOnClickListener {
                val newQuantity = cartItem.Quantity + 1
                quantity.text = newQuantity.toString()
                cartItem.Quantity = newQuantity
                total.text = (newQuantity * cartItem.Price.toInt()).toString()
                cartItem.TotalPrice = newQuantity * cartItem.Price.toInt()
                listener1.onAddItemClicked(cartItems)

            }
            minusButton.setOnClickListener {
                val newQuantity = cartItem.Quantity - 1
                if (newQuantity >= 0) {
                    quantity.text = newQuantity.toString()
                    cartItem.Quantity = newQuantity
                    total.text = (newQuantity * cartItem.Price.toInt()).toString()
                    cartItem.TotalPrice = newQuantity * cartItem.Price.toInt()
                    listener2.onMinusItemClicked(cartItems)

                }
            }
        }
    }

    interface OnRemoveItemClickListener {
        fun onRemoveItemClicked(position: Int)
    }
    interface OnPlusClickListener {
        fun onAddItemClicked(cartItems: List<CartItem>)
    }
    interface OnMinusItemClickListener {
        fun onMinusItemClicked(cartItems: List<CartItem>)
    }
}
