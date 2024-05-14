package com.project.auto_assist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ManagePartsAdapter(private var spareParts: List<SparePart>) :
    RecyclerView.Adapter<ManagePartsAdapter.ManagePartsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagePartsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.spare_part_item, parent, false)
        return ManagePartsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManagePartsViewHolder, position: Int) {
        val sparePart = spareParts[position]
        holder.bind(sparePart)
    }

    override fun getItemCount(): Int {
        return spareParts.size
    }

    inner class ManagePartsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageSparePart)
        private val Name: TextView = itemView.findViewById(R.id.txtSparepart)
        private val txtPriceText: TextView = itemView.findViewById(R.id.txtPrice)
        private val edit: AppCompatButton = itemView.findViewById(R.id.btnEditButton)
        private val delete: AppCompatButton = itemView.findViewById(R.id.btnDeleteButton)

        fun bind(sparePart: SparePart) {
            Name.text = sparePart.Name
            txtPriceText.text = buildString {
                append("PKR ")
                append(sparePart.Price)
            }
            Picasso.get().load(sparePart.ImageURL).into(image)

            edit.setOnClickListener {
                val intent = Intent(itemView.context, EditSparePart::class.java)
                val gson = Gson()
                val sparePartJson = gson.toJson(sparePart)
                intent.putExtra("sparePartJson", sparePartJson)
                itemView.context.startActivity(intent)
            }
            delete.setOnClickListener {
                showDeleteConfirmationDialog(sparePart, itemView)
            }
        }
    }

    private fun showDeleteConfirmationDialog(sparePart: SparePart, itemView: View) {

        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_registration_success, null)
        val builder = AlertDialog.Builder(itemView.context).setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val button = dialogView.findViewById<Button>(R.id.btnOk)
        button.setOnClickListener {
            deleteSparePart(sparePart, itemView)
            dialog.dismiss()
        }
    }

    private fun deleteSparePart(sparePart: SparePart, itemView: View) {
        val db = FirebaseFirestore.getInstance()
        db.collection("SpareParts").document(sparePart.Id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(itemView.context, "Spare part deleted", Toast.LENGTH_SHORT).show()
                val position = spareParts.indexOf(sparePart)
                if (position != -1) {
                    val newList = spareParts.toMutableList()
                    newList.removeAt(position)
                    spareParts = newList.toList()
                    notifyItemRemoved(position)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(itemView.context, "Failed to delete spare part", Toast.LENGTH_SHORT).show()
            }
    }

}

