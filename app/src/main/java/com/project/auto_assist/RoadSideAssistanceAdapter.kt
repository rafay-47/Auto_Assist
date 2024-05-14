package com.project.auto_assist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RoadSideAssistanceAdapter(private val context: Context, private var assistanceList: List<RoadSideAssistanceObject>,
                                private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RoadSideAssistanceAdapter.RoadSideAssistanceViewHolder>() {

    inner class RoadSideAssistanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCarName: TextView = itemView.findViewById(R.id.txtCarName) // Added TextView
        val txtPerson: TextView = itemView.findViewById(R.id.txtPerson)
        val txtContact: TextView = itemView.findViewById(R.id.txtContact)
        val txtService: TextView = itemView.findViewById(R.id.txtService)
        val Location: TextView = itemView.findViewById(R.id.txtLocation)
        val btnViewDetails: AppCompatButton = itemView.findViewById(R.id.btnViewDetails)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadSideAssistanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.roadside_assistance_list_item, parent, false)
        return RoadSideAssistanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoadSideAssistanceViewHolder, position: Int) {
        val assistance = assistanceList[position]
        holder.txtCarName.text = assistance.carBrand + " " + assistance.carModel + " " + assistance.carVariant
        holder.txtPerson.text = assistance.name
        holder.txtContact.text = assistance.mobile
        holder.txtService.text = assistance.serviceDetail
        holder.Location.text = assistance.location
        holder.btnViewDetails.setOnClickListener {
            showDeleteConfirmationDialog(assistance, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return assistanceList.size
    }

    private fun showDeleteConfirmationDialog(assistance: RoadSideAssistanceObject, itemView: View) {

        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.response_confirmation_dialog, null)
        val builder = AlertDialog.Builder(itemView.context).setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val button = dialogView.findViewById<Button>(R.id.btnOk)
        button.setOnClickListener {
            assistance.status = "Accepted"
            val ref = FirebaseFirestore.getInstance().collection("RoadSideAssistance").document(assistance.serviceId)
            val updates = mapOf<String, Any>(
                "serviceId" to assistance.serviceId,
                "userId" to assistance.userId,
                "name" to assistance.name,
                "mobile" to assistance.mobile,
                "location" to assistance.location,
                "carBrand" to assistance.carBrand,
                "carModel" to assistance.carModel,
                "carVariant" to assistance.carVariant,
                "serviceDetail" to assistance.serviceDetail,
                "description" to assistance.description,
                "imageUri" to assistance.imageUri,
                "status" to "Accepted"
            )
            ref.update(updates).addOnSuccessListener {
                val newList = assistanceList.filter { it.status != "Accepted" }
                assistanceList = newList
                notifyDataSetChanged()
                listener.onItemClick()
                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to Accept Request", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }
    interface OnItemClickListener {
        fun onItemClick()
    }
}