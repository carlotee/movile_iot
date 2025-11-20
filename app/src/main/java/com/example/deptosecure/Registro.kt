package com.example.deptosecure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class Registro : AppCompatActivity() {

    private lateinit var rut: EditText
    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var telefono: EditText
    private lateinit var clave: EditText
    private lateinit var confirmClave: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtLogin2: TextView

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular IDs
        rut = findViewById(R.id.inputRut)
        nombre = findViewById(R.id.inputNombre2)
        correo = findViewById(R.id.inputCorreo2)
        telefono = findViewById(R.id.inputTelefono)
        clave = findViewById(R.id.inputPassword2)
        confirmClave = findViewById(R.id.inputConfirmPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar2)
        txtLogin2 = findViewById(R.id.txtLogin2)

        queue = Volley.newRequestQueue(this)

        // 👉 Botón registrar
        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        // 👉 Texto "¿Ya tienes cuenta? Inicia sesión"
        txtLogin2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registrarUsuario() {

        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()
        val sConfirm = confirmClave.text.toString().trim()

        if (sClave != sConfirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://3.208.190.223/registro.php"

        val json = org.json.JSONObject().apply {
            put("rut", sRut)
            put("nombre", sNombre)
            put("email", sCorreo)
            put("telefono", sTelefono)
            put("password", sClave)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    if (success) {
                        // Navegar al login luego de registrar
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }
}
