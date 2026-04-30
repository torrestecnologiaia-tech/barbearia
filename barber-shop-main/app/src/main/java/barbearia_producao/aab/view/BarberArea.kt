package barbearia_producao.aab.view

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import barbearia_producao.aab.adapter.ManagementAdapter
import barbearia_producao.aab.databinding.ActivityBarberAreaBinding
import barbearia_producao.aab.model.Portfolio
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class BarberArea : AppCompatActivity() {

    private lateinit var binding: ActivityBarberAreaBinding
    private var imageUri: Uri? = null
    private val listPortfolio: MutableList<Portfolio> = mutableListOf()
    private lateinit var adapter: ManagementAdapter
    private var editingItem: Portfolio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Configura a lista de gerenciamento
        adapter = ManagementAdapter(this, listPortfolio, 
            onDelete = { item -> showDeleteConfirmation(item) },
            onEdit = { item -> startEditing(item) }
        )
        binding.rvManagement.layoutManager = LinearLayoutManager(this)
        binding.rvManagement.adapter = adapter

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                binding.imgPreview.setImageURI(it)
                binding.imgPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }

        binding.btnSelectImg.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            if (editingItem != null) {
                updateInFirestore()
            } else {
                uploadImage(it)
            }
        }
        
        loadPortfolio()
    }

    private fun loadPortfolio() {
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
                adapter.notifyDataSetChanged()
            }
    }

    private fun uploadImage(view: View) {
        val description = binding.editDescription.text.toString()

        if (imageUri == null) {
            message(view, "Escolha uma imagem!", "#E74C3C")
            return
        }

        if (description.isEmpty()) {
            message(view, "Preencha a descrição!", "#E74C3C")
            return
        }

        binding.btnUpload.isEnabled = false
        binding.btnUpload.text = "Enviando..."

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().reference.child("portfolio").child(filename)

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    saveInFirestore(view, url.toString(), description)
                }
            }
            .addOnFailureListener {
                resetUI()
                message(view, "Erro no Storage: ${it.message}", "#E74C3C")
            }
    }

    private fun saveInFirestore(view: View, url: String, description: String) {
        val db = FirebaseFirestore.getInstance()
        val work = hashMapOf(
            "url" to url,
            "description" to description,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("portfolio").add(work)
            .addOnSuccessListener {
                message(view, "Salvo com sucesso!", "#1ABC9C")
                resetUI()
            }
            .addOnFailureListener {
                resetUI()
                message(view, "Erro no Banco de Dados!", "#E74C3C")
            }
    }

    private fun startEditing(item: Portfolio) {
        editingItem = item
        binding.editDescription.setText(item.description)
        binding.btnUpload.text = "Salvar Alterações"
        binding.btnSelectImg.visibility = View.GONE
        binding.imgPreview.visibility = View.GONE
        message(binding.root, "Editando descrição...", "#3498DB")
    }

    private fun updateInFirestore() {
        val newDesc = binding.editDescription.text.toString()
        val item = editingItem ?: return
        
        FirebaseFirestore.getInstance().collection("portfolio").document(item.id!!)
            .update("description", newDesc)
            .addOnSuccessListener {
                message(binding.root, "Atualizado com sucesso!", "#1ABC9C")
                resetUI()
            }
    }

    private fun showDeleteConfirmation(item: Portfolio) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Trabalho")
            .setMessage("Tem certeza que deseja excluir este item do portfólio?")
            .setPositiveButton("Sim") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteItem(item: Portfolio) {
        val db = FirebaseFirestore.getInstance()
        db.collection("portfolio").document(item.id!!).delete()
            .addOnSuccessListener {
                message(binding.root, "Excluído com sucesso!", "#E74C3C")
            }
    }

    private fun resetUI() {
        binding.btnUpload.isEnabled = true
        binding.btnUpload.text = "Subir para o Portfólio"
        binding.editDescription.text.clear()
        binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        binding.btnSelectImg.visibility = View.VISIBLE
        binding.imgPreview.visibility = View.VISIBLE
        imageUri = null
        editingItem = null
    }

    private fun message(view: View, message: String, color: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.show()
    }
}
