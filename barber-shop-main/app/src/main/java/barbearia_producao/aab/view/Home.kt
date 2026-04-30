package barbearia_producao.aab.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import barbearia_producao.aab.R
import barbearia_producao.aab.adapter.ServicesAdapter
import barbearia_producao.aab.databinding.ActivityHomeBinding
import barbearia_producao.aab.model.Services

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
        val servicesOne = Services(R.drawable.img1, "Cortes")
        listServices.add(servicesOne)

        val servicesTwo = Services(R.drawable.img2, "Corte de barba")
        listServices.add(servicesTwo)

        val servicesThree = Services(R.drawable.img3, "Corte especiais")
        listServices.add(servicesThree)

        val servicesFour = Services(R.drawable.img4, "Tratamento")
        listServices.add(servicesFour)

        val servicesFive = Services(R.drawable.img1, "Luzes")
        listServices.add(servicesFive)
    }
}