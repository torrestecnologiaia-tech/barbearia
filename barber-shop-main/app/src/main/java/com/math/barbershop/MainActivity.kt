package com.math.barbershop

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import com.math.barbershop.databinding.ActivityMainBinding
import com.math.barbershop.view.Home

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {

            val name = binding.editName.text.toString()
            val password = binding.editPassword.text.toString()

            when {
                name.isEmpty() -> {
                    message(it, "Coloque seu nome!")
                }

                password.isEmpty() -> {
                    message(it, "Coloque sua senha!")
                }

                password.length <= 5 -> {
                    message(it, "A senha precisa ter pelo menos 6 caracteres!")
                }

                else -> {
                    navigateFromHome(name)
                }

            }
        }

        binding.textWhatsApp.setOnClickListener {
            val number = "5511992184639"
            val url = "https://api.whatsapp.com/send?phone=$number"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private fun message(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun navigateFromHome(name: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}