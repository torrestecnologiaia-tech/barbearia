package barbearia_producao.aab.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import barbearia_producao.aab.databinding.ActivityBarberAreaBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class BarberArea : AppCompatActivity() {

    private lateinit var binding: ActivityBarberAreaBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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
            uploadImage(it)
        }
    }

    private fun uploadImage(view: View) {
        val description = binding.editDescription.text.toString()

        if (imageUri == null) {
            message(view, "Escolha uma imagem do seu trabalho!", "#E74C3C")
            return
        }

        if (description.isEmpty()) {
            message(view, "Dê uma descrição ao seu trabalho!", "#E74C3C")
            return
        }

        // Desativa botões durante o upload
        binding.btnUpload.isEnabled = false
        binding.btnUpload.text = "Enviando..."
        message(view, "Enviando para o Portfólio...", "#2C3E50")

        val filename = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("portfolio").child(filename)

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    saveInFirestore(view, url.toString(), description)
                }
            }
            .addOnFailureListener {
                binding.btnUpload.isEnabled = true
                binding.btnUpload.text = "Subir para o Portfólio"
                message(view, "Erro no upload: ${it.message}", "#E74C3C")
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
                message(view, "Trabalho salvo com sucesso!", "#80CBC4")
                
                // Limpa campos
                binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                binding.imgPreview.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
                binding.editDescription.text.clear()
                imageUri = null
                
                binding.btnUpload.isEnabled = true
                binding.btnUpload.text = "Subir para o Portfólio"
            }
            .addOnFailureListener {
                binding.btnUpload.isEnabled = true
                binding.btnUpload.text = "Subir para o Portfólio"
                message(view, "Erro ao salvar no banco!", "#E74C3C")
            }
    }

    private fun message(view: View, message: String, color: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
}
