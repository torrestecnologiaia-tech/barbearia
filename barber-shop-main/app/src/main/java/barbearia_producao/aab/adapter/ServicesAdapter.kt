package barbearia_producao.aab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import barbearia_producao.aab.databinding.ServicesItemBinding
import barbearia_producao.aab.model.Services

class ServicesAdapter(
    private val context: Context,
    private val listServices: MutableList<Services>,
    private val onItemClick: (Services) -> Unit
) :
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val itemList = ServicesItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ServicesViewHolder(itemList)
    }

    override fun getItemCount() = listServices.size

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val service = listServices[position]
        holder.imgService.setImageResource(service.img!!)
        holder.textService.text = service.name
        holder.textPrice.text = service.price
        
        holder.itemView.setOnClickListener {
            onItemClick(service)
        }
    }

    inner class ServicesViewHolder(binding: ServicesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.imgService
        val textService = binding.textService
        val textPrice = binding.textPrice
    }
}