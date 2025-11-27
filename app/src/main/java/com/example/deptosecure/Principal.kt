package com.example.deptosecure

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private lateinit var txtEstado: TextView
    private lateinit var btnAbrir: Button
    private var idUsuarioLogueado: String = "0"
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

        // Vincular vistas
        val btnGestionSensores = findViewById<Button>(R.id.btngs1)
        val btnHistorial = findViewById<Button>(R.id.btnVerHistorial)
        val btnCrearUsuario = findViewById<Button>(R.id.btnCrearUsuario)

        txtEstado = findViewById(R.id.txtEstadoBarrera)
        btnAbrir = findViewById(R.id.btnAbrirBarrera)

        // Recuperar datos
        val rol = intent.getStringExtra("ROL_USUARIO") ?: "OPERADOR"
        idUsuarioLogueado = intent.getStringExtra("ID_USUARIO") ?: "0"

        // Configuración según ROL
        if (rol == "ADMIN") {
            btnGestionSensores.visibility = View.VISIBLE
            btnCrearUsuario.visibility = View.VISIBLE

            btnGestionSensores.setOnClickListener {
                val intent = Intent(this, GestionSensores::class.java)
                startActivity(intent)
            }

            // ⭐ CORRECCIÓN AQUÍ: Apuntamos a la nueva actividad ⭐
            btnCrearUsuario.setOnClickListener {
                val intent = Intent(this, CrearUsuarioActivity::class.java)
                startActivity(intent)
            }

        } else {
            btnGestionSensores.visibility = View.GONE
            btnCrearUsuario.visibility = View.GONE
        }

        // Resto de botones
        btnAbrir.setOnClickListener {
            enviarComandoBarrera("ABRIR")
        }

        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    private fun enviarComandoBarrera(accion: String) {
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put("accion", accion)
        params.put("id_usuario", idUsuarioLogueado)

        val request = JsonObjectRequest(Request.Method.POST, URL_API, params,
            { response ->
                val mensaje = response.optString("msg", "Comando enviado")
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                actualizarUI(accion)
            },
            { error ->
                val msg = when (error) {
                    is com.android.volley.TimeoutError -> "Tiempo de espera agotado..."
                    is com.android.volley.NoConnectionError -> "Sin conexión a internet"
                    is com.android.volley.ServerError -> "Error en el servidor (500)"
                    else -> "Error: ${error.message}"
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                error.printStackTrace()
            }
        )

        request.retryPolicy = com.android.volley.DefaultRetryPolicy(
            10000,
            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(request)
    }

    private fun actualizarUI(estado: String) {
        if (estado == "ABRIR") {
            txtEstado.text = "ABIERTA"
            txtEstado.setTextColor(Color.parseColor("#4CAF50"))

            Handler(Looper.getMainLooper()).postDelayed({
                txtEstado.text = "CERRADA"
                txtEstado.setTextColor(Color.parseColor("#D32F2F"))
            }, 6000)

        } else {
            txtEstado.text = "CERRADA"
            txtEstado.setTextColor(Color.parseColor("#D32F2F"))
        }
    }
}