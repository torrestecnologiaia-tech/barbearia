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

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val day = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val month = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else (monthOfYear + 1).toString()

            data = "$day / $month / $year"
        }

        val timePicker = binding.timePicker
        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val min = if (minute < 10) "0$minute" else minute.toString()
            hora = "$hourOfDay:$min"
        }

        binding.btnAgendar.setOnClickListener {
            val barbeiro = if (binding.barber1.isChecked) "Diego" else ""

            when {
                hora.isEmpty() -> {
                    message(it, "Preencha o horário!", "#FF0000")
                }
                data.isEmpty() -> {
                    message(it, "Preencha a data!", "#FF0000")
                }
                barbeiro.isEmpty() -> {
                    message(it, "Escolha o profissional!", "#FF0000")
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

        // Usa ID aleatório para não sobrepor agendamentos do mesmo cliente
        db.collection("agendamento").add(dataUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                message(view, "Agendamento realizado com sucesso!", "#80CBC4")
                // Finaliza a tela após 1.5 segundos
                view.postDelayed({ finish() }, 1500)
            } else {
                message(view, "Erro ao agendar. Tente novamente!", "#FF0000")
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