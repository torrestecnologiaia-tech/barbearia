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
            mensage(view, "Escolha uma imagem primeiro!", "#FF0000")
            return
        }

        if (description.isEmpty()) {
            mensage(view, "Dê uma descrição ao seu trabalho!", "#FF0000")
            return
        }

        mensage(view, "Subindo imagem... aguarde.", "#2C3E50")

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/portfolio/$filename")

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    saveInFirestore(view, url.toString(), description)
                }
            }
            .addOnFailureListener {
                mensage(view, "Erro ao enviar imagem: ${it.message}", "#FF0000")
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
                mensage(view, "Trabalho salvo no portfólio!", "#80CBC4")
                binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                binding.editDescription.text.clear()
                imageUri = null
            }
            .addOnFailureListener {
                mensage(view, "Erro ao salvar dados!", "#FF0000")
            }
    }

    private fun mensage(view: View, mensage: String, color: String) {
        val snackbar = Snackbar.make(view, mensage, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
}
