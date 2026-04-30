package barbearia_producao.aab.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import barbearia_producao.aab.R
import barbearia_producao.aab.adapter.PortfolioAdapter
import barbearia_producao.aab.adapter.ServicesAdapter
import barbearia_producao.aab.databinding.ActivityHomeBinding
import barbearia_producao.aab.model.Portfolio
import barbearia_producao.aab.model.Services
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var portfolioAdapter: PortfolioAdapter
    private val listServices: MutableList<Services> = mutableListOf()
    private val listPortfolio: MutableList<Portfolio> = mutableListOf()
    
    private var selectedService: String? = null
    private var selectedPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val name = intent.extras?.getString("name")

        binding.textName.text = "Bem-vindo(a), ${name}!"
        
        // (Portfolio code same as before...)
        val recyclerViewPortfolio = binding.recyclerViewPortfolio
        recyclerViewPortfolio.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        portfolioAdapter = PortfolioAdapter(this, listPortfolio)
        recyclerViewPortfolio.setHasFixedSize(true)
        recyclerViewPortfolio.adapter = portfolioAdapter
        
        // (Services code updated to fetch from Firebase)
        val recyclerViewServices = binding.recycleViewService
        recyclerViewServices.layoutManager = GridLayoutManager(this, 2)
        servicesAdapter = ServicesAdapter(this, listServices) { service ->
            selectedService = service.name
            selectedPrice = service.price
            
            val intent = Intent(this, Scheduling::class.java)
            intent.putExtra("name", name)
            intent.putExtra("service", selectedService)
            intent.putExtra("price", selectedPrice)
            startActivity(intent)
        }
        recyclerViewServices.adapter = servicesAdapter
        
        getDynamicServices() // Atualizado
        getPortfolio()

        binding.btnSchedule.setOnClickListener {
            if (listServices.isEmpty()) return@setOnClickListener
            
            if (selectedService == null) {
                selectedService = listServices[0].name
                selectedPrice = listServices[0].price
            }
            
            val intent = Intent(this, Scheduling::class.java)
            intent.putExtra("name", name)
            intent.putExtra("service", selectedService)
            intent.putExtra("price", selectedPrice)
            startActivity(intent)
        }
        
        binding.btnBarberArea.setOnClickListener {
            showPasswordDialog()
        }
    }

    private fun showPasswordDialog() {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        editText.hint = "Senha do Diego"

        AlertDialog.Builder(this)
            .setTitle("Área Restrita")
            .setMessage("Por favor, insira a senha para acessar:")
            .setView(editText)
            .setPositiveButton("Entrar") { _, _ ->
                val password = editText.text.toString()
                if (password == "23111991") {
                    val intent = Intent(this, BarberArea::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.root, "Senha incorreta!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun getDynamicServices() {
        val db = FirebaseFirestore.getInstance()
        db.collection("servicos")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                listServices.clear()
                
                if (value == null || value.isEmpty) {
                    // Se estiver vazio no Firebase, preenche com padrões e salva lá
                    seedDefaultServices()
                } else {
                    value.documents.forEach { doc ->
                        val name = doc.getString("name") ?: ""
                        val price = doc.getString("price") ?: ""
                        // Nota: Para ícones, usaremos um ícone de tesoura padrão se for dinâmico
                        listServices.add(Services(R.drawable.img1, name, price))
                    }
                    servicesAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun seedDefaultServices() {
        val db = FirebaseFirestore.getInstance()
        val defaults = listOf(
            mapOf("name" to "Cortes", "price" to "R$ 30,00"),
            mapOf("name" to "Corte de barba", "price" to "R$ 20,00"),
            mapOf("name" to "Corte especiais", "price" to "R$ 50,00"),
            mapOf("name" to "Tratamento", "price" to "R$ 40,00")
        )
        defaults.forEach { db.collection("servicos").add(it) }
    }
    
    private fun getPortfolio() {
        val db = FirebaseFirestore.getInstance()
        db.collection("portfolio")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                listPortfolio.clear()
                value?.documents?.forEach { doc ->
                    val portfolio = doc.toObject(Portfolio::class.java)
                    if (portfolio != null) {
                        portfolio.id = doc.id
                        listPortfolio.add(portfolio)
                    }
                }
                portfolioAdapter.notifyDataSetChanged()
            }
    }
}