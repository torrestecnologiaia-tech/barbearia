package com.math.barbershop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.math.barbershop.databinding.ServicesItemBinding
import com.math.barbershop.model.Services

class ServicesAdapter(
    private val context: Context,
    private val listServices: MutableList<Services>
) :
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val itemList = ServicesItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ServicesViewHolder(itemList)
    }

    override fun getItemCount() = listServices.size

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        holder.imgService.setImageResource(listServices[position].img!!)
        holder.textService.text = listServices[position].name
    }

    inner class ServicesViewHolder(binding: ServicesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.imgService
        val textService = binding.textService
    }
}