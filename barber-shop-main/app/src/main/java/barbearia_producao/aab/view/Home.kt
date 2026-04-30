package barbearia_producao.aab.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import barbearia_producao.aab.R
import barbearia_producao.aab.adapter.PortfolioAdapter
import barbearia_producao.aab.adapter.ServicesAdapter
import barbearia_producao.aab.databinding.ActivityHomeBinding
import barbearia_producao.aab.model.Portfolio
import barbearia_producao.aab.model.Services
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Home : AppCompatActivity() {

    // DIH CORTE'S - Home Activity com Galeria e Serviços

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var portfolioAdapter: PortfolioAdapter
    private val listServices: MutableList<Services> = mutableListOf()
    private val listPortfolio: MutableList<Portfolio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val name = intent.extras?.getString("name")

        binding.textName.text = "Bem vindo(a), ${name}"
        
        // Configura Serviços
        val recyclerViewServices = binding.recycleViewService
        recyclerViewServices.layoutManager = GridLayoutManager(this, 2)
        servicesAdapter = ServicesAdapter(this, listServices) { service ->
            val intent = Intent(this, Scheduling::class.java)
            intent.putExtra("name", name)
            intent.putExtra("service", service.name)
            intent.putExtra("price", service.price)
            startActivity(intent)
        }
        recyclerViewServices.setHasFixedSize(true)
        recyclerViewServices.adapter = servicesAdapter
        
        // Configura Portfólio
        val recyclerViewPortfolio = binding.recyclerViewPortfolio
        recyclerViewPortfolio.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        portfolioAdapter = PortfolioAdapter(this, listPortfolio)
        recyclerViewPortfolio.setHasFixedSize(true)
        recyclerViewPortfolio.adapter = portfolioAdapter
        
        getService()
        getPortfolio()

        binding.btnSchedule.setOnClickListener {
            val intent = Intent(this, Scheduling::class.java)
            intent.putExtra("name", name)
            intent.putExtra("service", listServices[0].name)
            intent.putExtra("price", listServices[0].price)
            startActivity(intent)
        }
        
        binding.btnBarberArea.setOnClickListener {
            val intent = Intent(this, BarberArea::class.java)
            startActivity(intent)
        }
    }

    private fun getService() {
        val servicesOne = Services(R.drawable.img1, "Cortes", "R$ 30,00")
        listServices.add(servicesOne)

        val servicesTwo = Services(R.drawable.img2, "Corte de barba", "R$ 20,00")
        listServices.add(servicesTwo)

        val servicesThree = Services(R.drawable.img3, "Corte especiais", "R$ 50,00")
        listServices.add(servicesThree)

        val servicesFour = Services(R.drawable.img4, "Tratamento", "R$ 40,00")
        listServices.add(servicesFour)

        val servicesFive = Services(R.drawable.img1, "Luzes", "R$ 100,00")
        listServices.add(servicesFive)
    }
    
    private fun getPortfolio() {
        val db = FirebaseFirestore.getInstance()
        db.collection("portfolio")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    listPortfolio.clear()
                    val documents = value?.documents
                    if (documents != null) {
                        for (doc in documents) {
                            val portfolio = doc.toObject(Portfolio::class.java)
                            if (portfolio != null) {
                                listPortfolio.add(portfolio)
                            }
                        }
                        portfolioAdapter.notifyDataSetChanged()
                    }
                }
            }
    }
}