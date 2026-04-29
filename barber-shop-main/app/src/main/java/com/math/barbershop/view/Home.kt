package com.math.barbershop.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.math.barbershop.R
import com.math.barbershop.adapter.ServicesAdapter
import com.math.barbershop.databinding.ActivityHomeBinding
import com.math.barbershop.model.Services

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicesAdapter: ServicesAdapter
    private val listServices: MutableList<Services> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val name = intent.extras?.getString("name")

        binding.textName.text = "Bem vindo(a), ${name}"
        val recyclerViewServices = binding.recycleViewService
        recyclerViewServices.layoutManager = GridLayoutManager(this, 2)
        servicesAdapter = ServicesAdapter(this, listServices)
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.adapter = servicesAdapter
        getService()

        binding.btnSchedule.setOnClickListener {
            val intent = Intent(this, Scheduling::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }

    private fun getService() {
        val servicesOne = Services(R.drawable.img1, "Corte de Cabelo")
        listServices.add(servicesOne)

        val servicesTwo = Services(R.drawable.img2, "Corte de barba")
        listServices.add(servicesTwo)

        val servicesThree = Services(R.drawable.img3, "Lavagem de cabelo")
        listServices.add(servicesThree)

        val servicesFour = Services(R.drawable.img4, "Tratamento de cabelo")
        listServices.add(servicesFour)
    }
}