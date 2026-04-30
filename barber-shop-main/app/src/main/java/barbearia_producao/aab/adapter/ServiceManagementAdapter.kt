package barbearia_producao.aab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import barbearia_producao.aab.R
import barbearia_producao.aab.model.Services

class ServiceManagementAdapter(
    private val context: Context,
    private val list: MutableList<Services>,
    private val onDelete: (String) -> Unit,
    private val onEdit: (Services) -> Unit
) : RecyclerView.Adapter<ServiceManagementAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.service_management_item, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = list[position]
        holder.txtName.text = service.name
        holder.txtPrice.text = service.price

        holder.btnDelete.setOnClickListener { service.id?.let { id -> onDelete(id) } }
        holder.btnEdit.setOnClickListener { onEdit(service) }
    }

    override fun getItemCount(): Int = list.size

    inner class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtServiceName)
        val txtPrice: TextView = view.findViewById(R.id.txtServicePrice)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteService)
        val btnEdit: Button = view.findViewById(R.id.btnEditService)
    }
}
