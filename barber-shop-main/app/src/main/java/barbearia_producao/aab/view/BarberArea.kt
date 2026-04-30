package barbearia_producao.aab.view

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import barbearia_producao.aab.adapter.ManagementAdapter
import barbearia_producao.aab.adapter.ServiceManagementAdapter
import barbearia_producao.aab.databinding.ActivityBarberAreaBinding
import barbearia_producao.aab.model.Portfolio
import barbearia_producao.aab.model.Services
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class BarberArea : AppCompatActivity() {

    private lateinit var binding: ActivityBarberAreaBinding
    
    // Portfolio
    private var imageUri: Uri? = null
    private val listPortfolio: MutableList<Portfolio> = mutableListOf()
    private lateinit var portfolioAdapter: ManagementAdapter
    private var editingPortfolio: Portfolio? = null

    // Services
    private val listServices: MutableList<Services> = mutableListOf()
    private lateinit var serviceAdapter: ServiceManagementAdapter
    private var editingService: Services? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupPortfolioList()
        setupServicesList()

        // Listeners Portfolio
        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                binding.imgPreview.setImageURI(it)
                binding.imgPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }
        binding.btnSelectImg.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.btnUpload.setOnClickListener {
            if (editingPortfolio != null) updatePortfolioFirestore() else uploadImagePortfolio(it)
        }

        // Listeners Services
        binding.btnAddService.setOnClickListener {
            if (editingService != null) updateServiceFirestore() else saveServiceFirestore()
        }
        
        loadPortfolio()
        loadServices()
    }

    // --- SERVICES LOGIC ---

    private fun setupServicesList() {
        serviceAdapter = ServiceManagementAdapter(this, listServices,
            onDelete = { id -> showDeleteServiceDialog(id) },
            onEdit = { service -> startEditingService(service) }
        )
        binding.rvServicesManagement.layoutManager = LinearLayoutManager(this)
        binding.rvServicesManagement.adapter = serviceAdapter
    }

    private fun loadServices() {
        FirebaseFirestore.getInstance().collection("servicos")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                listServices.clear()
                value?.documents?.forEach { doc ->
                    val name = doc.getString("name") ?: ""
                    val price = doc.getString("price") ?: ""
                    listServices.add(Services(null, name, price, doc.id))
                }
                serviceAdapter.notifyDataSetChanged()
            }
    }

    private fun saveServiceFirestore() {
        val name = binding.editServiceName.text.toString()
        val price = binding.editServicePrice.text.toString()

        if (name.isEmpty() || price.isEmpty()) {
            message(binding.root, "Preencha nome e valor!", "#E74C3C")
            return
        }

        val service = mapOf("name" to name, "price" to price)
        FirebaseFirestore.getInstance().collection("servicos").add(service)
            .addOnSuccessListener {
                message(binding.root, "Serviço adicionado!", "#1ABC9C")
                resetServiceUI()
            }
    }

    private fun startEditingService(service: Services) {
        editingService = service
        binding.editServiceName.setText(service.name)
        binding.editServicePrice.setText(service.price)
        binding.btnAddService.text = "ATUALIZAR SERVIÇO"
        message(binding.root, "Editando serviço...", "#3498DB")
    }

    private fun updateServiceFirestore() {
        val name = binding.editServiceName.text.toString()
        val price = binding.editServicePrice.text.toString()
        val id = editingService?.id ?: return

        FirebaseFirestore.getInstance().collection("servicos").document(id)
            .update("name", name, "price", price)
            .addOnSuccessListener {
                message(binding.root, "Serviço atualizado!", "#1ABC9C")
                resetServiceUI()
            }
    }

    private fun showDeleteServiceDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Serviço")
            .setMessage("Tem certeza?")
            .setPositiveButton("Sim") { _, _ ->
                FirebaseFirestore.getInstance().collection("servicos").document(id).delete()
                    .addOnSuccessListener { message(binding.root, "Serviço removido!", "#E74C3C") }
            }
            .setNegativeButton("Não", null).show()
    }

    private fun resetServiceUI() {
        binding.editServiceName.text.clear()
        binding.editServicePrice.text.clear()
        binding.btnAddService.text = "SALVAR SERVIÇO"
        editingService = null
    }

    // --- PORTFOLIO LOGIC ---

    private fun setupPortfolioList() {
        portfolioAdapter = ManagementAdapter(this, listPortfolio, 
            onDelete = { item -> showDeletePortfolioDialog(item) },
            onEdit = { item -> startEditingPortfolio(item) }
        )
        binding.rvManagement.layoutManager = LinearLayoutManager(this)
        binding.rvManagement.adapter = portfolioAdapter
    }

    private fun uploadImagePortfolio(view: View) {
        val description = binding.editDescription.text.toString()
        if (imageUri == null || description.isEmpty()) {
            message(view, "Preencha tudo!", "#E74C3C"); return
        }

        binding.btnUpload.isEnabled = false
        binding.btnUpload.text = "Enviando..."
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().reference.child("portfolio").child(filename)

        ref.putFile(imageUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                savePortfolioFirestore(view, url.toString(), description)
            }
        }.addOnFailureListener {
            resetPortfolioUI()
            message(view, "Erro no Storage: ${it.message}", "#E74C3C")
        }
    }

    private fun savePortfolioFirestore(view: View, url: String, description: String) {
        val work = hashMapOf("url" to url, "description" to description, "timestamp" to System.currentTimeMillis())
        FirebaseFirestore.getInstance().collection("portfolio").add(work)
            .addOnSuccessListener { message(view, "Publicado!", "#1ABC9C"); resetPortfolioUI() }
            .addOnFailureListener { resetPortfolioUI(); message(view, "Erro no Banco!", "#E74C3C") }
    }

    private fun startEditingPortfolio(item: Portfolio) {
        editingPortfolio = item
        binding.editDescription.setText(item.description)
        binding.btnUpload.text = "SALVAR ALTERAÇÕES"
        binding.btnSelectImg.visibility = View.GONE
        binding.imgPreview.visibility = View.GONE
    }

    private fun updatePortfolioFirestore() {
        val newDesc = binding.editDescription.text.toString()
        val item = editingPortfolio ?: return
        FirebaseFirestore.getInstance().collection("portfolio").document(item.id!!)
            .update("description", newDesc)
            .addOnSuccessListener { message(binding.root, "Descrição atualizada!", "#1ABC9C"); resetPortfolioUI() }
    }

    private fun showDeletePortfolioDialog(item: Portfolio) {
        AlertDialog.Builder(this).setTitle("Excluir Foto").setMessage("Tem certeza?").setPositiveButton("Sim") { _, _ ->
            FirebaseFirestore.getInstance().collection("portfolio").document(item.id!!).delete()
                .addOnSuccessListener { message(binding.root, "Excluído!", "#E74C3C") }
        }.setNegativeButton("Não", null).show()
    }

    private fun loadPortfolio() {
        FirebaseFirestore.getInstance().collection("portfolio")
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

    private fun resetPortfolioUI() {
        binding.btnUpload.isEnabled = true
        binding.btnUpload.text = "PUBLICAR NO PORTFÓLIO"
        binding.editDescription.text.clear()
        binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        binding.btnSelectImg.visibility = View.VISIBLE
        binding.imgPreview.visibility = View.VISIBLE
        imageUri = null
        editingPortfolio = null
    }

    private fun message(view: View, message: String, color: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.show()
    }
}
