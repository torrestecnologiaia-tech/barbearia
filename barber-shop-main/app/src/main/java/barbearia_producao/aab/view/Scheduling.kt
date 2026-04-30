package barbearia_producao.aab.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import barbearia_producao.aab.databinding.ActivitySchedulingBinding
import java.util.Calendar

class Scheduling : AppCompatActivity() {

    private lateinit var binding: ActivitySchedulingBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val name = intent.extras?.getString("name") ?: "Cliente"
        val service = intent.extras?.getString("service") ?: "Corte"
        val price = intent.extras?.getString("price") ?: "R$ 30,00"

        // Inicializar com os valores atuais dos pickers para evitar erro se o usuário não mexer neles
        val initialDay = binding.datePicker.dayOfMonth
        val initialMonth = binding.datePicker.month + 1
        val initialYear = binding.datePicker.year
        data = String.format("%02d / %02d / %d", initialDay, initialMonth, initialYear)

        val initialHour = binding.timePicker.hour
        val initialMinute = binding.timePicker.minute
        hora = String.format("%02d:%02d", initialHour, initialMinute)

        binding.datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val month = monthOfYear + 1
            data = String.format("%02d / %02d / %d", dayOfMonth, month, year)
        }

        binding.timePicker.setIs24HourView(true)
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            hora = String.format("%02d:%02d", hourOfDay, minute)
        }

        binding.btnAgendar.setOnClickListener {
            val barbeiro = if (binding.barber1.isChecked) "Diego" else ""

            when {
                data.isEmpty() -> {
                    message(it, "Escolha uma data!", "#E74C3C")
                }
                hora.isEmpty() -> {
                    message(it, "Escolha um horário!", "#E74C3C")
                }
                barbeiro.isEmpty() -> {
                    message(it, "O profissional Diego deve estar selecionado!", "#E74C3C")
                }
                else -> {
                    saveScheduling(it, name, barbeiro, data, hora, service, price)
                }
            }
        }
    }

    private fun saveScheduling(view: View, cliente: String, barbeiro: String, data: String, hora: String, service: String, price: String) {
        val db = FirebaseFirestore.getInstance()

        val dataUser = hashMapOf(
            "cliente" to cliente,
            "barbeiro" to barbeiro,
            "data" to data,
            "hora" to hora,
            "servico" to service,
            "valor" to price,
            "status" to "pendente",
            "timestamp" to System.currentTimeMillis()
        )

        // Desativa o botão para evitar cliques duplos
        binding.btnAgendar.isEnabled = false
        binding.btnAgendar.text = "Salvando..."

        db.collection("agendamento").add(dataUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                message(view, "Agendamento realizado! Veremos você em breve.", "#80CBC4")
                view.postDelayed({ finish() }, 1500)
            } else {
                binding.btnAgendar.isEnabled = true
                binding.btnAgendar.text = "Agendar"
                message(view, "Erro do Firebase. Verifique suas regras no console!", "#E74C3C")
            }
        }
    }

    private fun message(view: View, message: String, color: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
}