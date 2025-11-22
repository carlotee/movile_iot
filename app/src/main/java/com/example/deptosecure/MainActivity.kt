package com.example.deptosecure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var usu: EditText
    private lateinit var clave: EditText
    private lateinit var btn: Button
    private lateinit var datos: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usu = findViewById(R.id.usuario)
        clave = findViewById(R.id.clave)
        btn = findViewById(R.id.btningresar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)

        datos = Volley.newRequestQueue(this)

        btnIrRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        btn.setOnClickListener {
            val sUsu = usu.text.toString().trim()
            val sPass = clave.text.toString().trim()

            if (sUsu.isEmpty() || sPass.isEmpty()) {
                Toast.makeText(this, "Por favor complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                consultarDatos(sUsu, sPass)
            }
        }
    }

    private fun consultarDatos(usuario: String, pass: String) {
        val url = "http://3.208.190.223/apiconsultausu.php"

        // 1. Creamos el objeto JSON para enviar los datos
        val jsonParams = JSONObject()
        try {
            jsonParams.put("usu", usuario)
            jsonParams.put("pass", pass)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // 2. Usamos JsonObjectRequest para enviar y recibir JSON
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonParams, // Enviamos el JSON que creamos
            { response -> // La respuesta ya es un objeto JSON, no hay que convertirla
                val estado = response.optString("estado")
                val mensaje = response.optString("msg")

                if (estado == "1") {
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                    val rolUsuario = response.optString("rol", "OPERADOR")

                    val intent = Intent(this, Principal::class.java)
                    intent.putExtra("ROL_USUARIO", rolUsuario)

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                error.printStackTrace()
            }
        )

        datos.add(request)
    }
}