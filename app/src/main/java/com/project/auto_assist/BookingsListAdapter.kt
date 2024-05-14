package com.project.auto_assist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class BookingsListAdapter(private val context: Context, private val serviceList: List<Service>) :
    RecyclerView.Adapter<BookingsListAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPerson: TextView = itemView.findViewById(R.id.txtPerson)
        val txtContact: TextView = itemView.findViewById(R.id.txtContact)
        val txtService: TextView = itemView.findViewById(R.id.txtService)
        val btnViewDetails: AppCompatButton = itemView.findViewById(R.id.btnViewDetails)
        val txtCarName: TextView = itemView.findViewById(R.id.txtCarName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.booking_list_item, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = serviceList[position]
        holder.txtPerson.text = service.name
        holder.txtContact.text = service.mobile
        holder.txtService.text = service.serviceDetail
        holder.txtCarName.text = service.carBrand + " " + service.carModel + " " + service.carVariant

        holder.btnViewDetails.setOnClickListener {
            // Handle button click event
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }
}
