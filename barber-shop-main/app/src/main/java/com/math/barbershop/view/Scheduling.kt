package com.math.barbershop.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.math.barbershop.databinding.ActivitySchedulingBinding
import java.util.Calendar

class Scheduling : AppCompatActivity() {

    private lateinit var binding: ActivitySchedulingBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var date: String = ""
    private var hour: String = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val name = intent.extras?.getString("name").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMOnth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMOnth)

            var day = dayOfMOnth.toString()
            val month: String

            if (dayOfMOnth < 10) {
                day = "0$dayOfMOnth"
            }
            if (monthOfYear < 10) {
                month = "" + (monthOfYear + 1)
            } else {
                month = (monthOfYear + 1).toString()
            }

            date = "$day / $month / $year"
        }

        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val minuto: String

            if (minute < 10) {
                minuto = "0$minute"
            } else {
                minuto = minute.toString()
            }

            hour = "$hourOfDay:$minuto" // 19:00
        }

        binding.timePicker.setIs24HourView(true)

        binding.btnAgendar.setOnClickListener {

            val barber1 = binding.barber1
            val barber2 = binding.barber2
            val barber3 = binding.barber3

            when {
                hour.isEmpty() -> {
                    mensage(it, "Preenchar o horário!", "#FF0000")
                }

                hour < "8:00" && hour > "19:00" -> {
                    mensage(
                        it,
                        "Barber Shop está fechado - horário de atedimento das 08:00 as 19:00!",
                        "#FF0000"
                    )
                }

                date.isEmpty() -> {
                    mensage(it, "Coloque uma data", "#FF0000")
                }

                barber1.isChecked && date.isNotEmpty() && hour.isNotEmpty() -> {
                    saveScheduling(it, name, "Cristiano Ronaldo", date, hour)
                }

                barber2.isChecked && date.isNotEmpty() && hour.isNotEmpty() -> {
                    saveScheduling(it, name, "Bob Firmino", date, hour)
                }

                barber3.isChecked && date.isNotEmpty() && hour.isNotEmpty() -> {
                    saveScheduling(it, name, "Gabigol da Silva", date, hour)
                }

                else -> {
                    mensage(it, "Escolha um barbeiro", "#FF0000")
                }
            }
        }
    }

    private fun mensage(view: View, mensage: String, color: String) {
        val snackbar = Snackbar.make(view, mensage, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(color))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun saveScheduling(
        view: View,
        cliente: String,
        barbeiro: String,
        data: String,
        hora: String
    ) {

        val db = FirebaseFirestore.getInstance()

        val dataUser = hashMapOf(
            "cliente" to cliente,
            "barbeiro" to barbeiro,
            "data" to data,
            "hora" to hora,
        )

        db.collection("agendamento").document(cliente).set(dataUser).addOnCompleteListener {
            mensage(view, "Agendamento realizado com sucesso!", "#80CBC4")
        }.addOnFailureListener {
            mensage(view, "Erro ao salvar!", "#FF0000")
        }
    }

}