package com.example.deptosecure

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Principal : AppCompatActivity() {

    // UI Components
    private lateinit var txtEstado: TextView
    private lateinit var btnAbrir: Button
    private lateinit var btnCerrar: Button

    private val URL_API = "http://3.208.190.223/control_barrera.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Vincular vistas
        val btnGestionSensores = findViewById<Button>(R.id.btngs1)
        val btnHistorial = findViewById<Button>(R.id.btnVerHistorial)

        txtEstado = findViewById(R.id.txtEstadoBarrera)
        btnAbrir = findViewById(R.id.btnAbrirBarrera)
        btnCerrar = findViewById(R.id.btnCerrarBarrera)

        // ⭐ 2. GESTIÓN DE ROLES ⭐
        // Recuperamos el dato que nos envió MainActivity
        val rol = intent.getStringExtra("ROL_USUARIO") ?: "OPERADOR"

        if (rol == "ADMIN") {
            // Si es Admin, dejamos el botón visible y activo
            btnGestionSensores.visibility = View.VISIBLE
            btnGestionSensores.setOnClickListener {
                val intent = Intent(this, GestionSensores::class.java)
                startActivity(intent)
            }
        } else {
            // Si NO es Admin (es OPERADOR), ocultamos el botón
            btnGestionSensores.visibility = View.GONE
        }

        // 3. Lógica de Barrera (Disponible para todos)
        btnAbrir.setOnClickListener {
            enviarComandoBarrera("ABRIR")
        }

        btnCerrar.setOnClickListener {
            enviarComandoBarrera("CERRAR")
        }

        // 4. Navegación a Historial (Disponible para todos)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    private fun enviarComandoBarrera(accion: String) {
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put("accion", accion)

        val request = JsonObjectRequest(Request.Method.POST, URL_API, params,
            { response ->
                val mensaje = response.optString("msg", "Comando enviado")
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                actualizarUI(accion)
            },
            { error ->
                // ⭐ DIAGNÓSTICO AVANZADO MODIFICADO ⭐
                val networkResponse = error.networkResponse

                if (networkResponse != null) {
                    // Si el servidor respondió algo (ej: 404, 500, 403)
                    val statusCode = networkResponse.statusCode
                    // Convertimos los datos a texto para ver el error real (HTML o texto)
                    val htmlData = String(networkResponse.data)

                    // Muestra el error real en pantalla
                    Toast.makeText(this, "Error Servidor ($statusCode)", Toast.LENGTH_LONG).show()

                    // Muestra el detalle en la consola de Android Studio (Logcat)
                    println("ERROR_VOLLEY_DETALLE: $htmlData")

                } else {
                    // Si ni siquiera hubo respuesta (Timeout o Sin Internet)
                    val msg = when (error) {
                        is com.android.volley.TimeoutError -> "Tiempo de espera agotado"
                        is com.android.volley.NoConnectionError -> "No hay conexión con el servidor"
                        else -> "Error desconocido: ${error.message}"
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
                error.printStackTrace()
            }
        )

        // ⭐ POLÍTICA DE REINTENTOS (RETRY POLICY) ⭐
        request.retryPolicy = com.android.volley.DefaultRetryPolicy(
            10000, // 10 segundos
            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(request)
    }

    private fun actualizarUI(estado: String) {
        if (estado == "ABRIR") {
            txtEstado.text = "ABIERTA"
            txtEstado.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            txtEstado.text = "CERRADA"
            txtEstado.setTextColor(Color.parseColor("#F44336"))
        }
    }
}