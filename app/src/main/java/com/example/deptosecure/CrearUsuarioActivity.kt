package com.example.deptosecure

import android.os.Bundle
import android.util.Patterns
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

class CrearUsuarioActivity : AppCompatActivity() {

    private lateinit var rut: EditText
    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var telefono: EditText
    private lateinit var clave: EditText
    private lateinit var confirmClave: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_usuario) // Usa el nuevo XML

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

        queue = Volley.newRequestQueue(this)

        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }
    }

    private fun validarCampos(): Boolean {
        // ... (Tu misma lógica de validación aquí) ...
        // Copia las validaciones del archivo Registro.kt original si quieres
        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()
        val sConfirm = confirmClave.text.toString().trim()

        if (sNombre.isEmpty()) { nombre.error = "Obligatorio"; return false }
        if (sRut.isEmpty()) { rut.error = "Obligatorio"; return false }
        if (sCorreo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(sCorreo).matches()) {
            correo.error = "Inválido"; return false
        }
        if (sTelefono.isEmpty()) { telefono.error = "Obligatorio"; return false }
        if (sClave.isEmpty() || sClave.length < 4) { clave.error = "Mínimo 4 caracteres"; return false }
        if (sClave != sConfirm) { confirmClave.error = "No coinciden"; return false }

        return true
    }

    private fun registrarUsuario() {
        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()

        val rutLimpio = sRut.replace(".", "").replace("-", "")

        // Usamos el mismo PHP que ya funciona
        val url = "http://3.208.190.223/registro.php"

        val json = JSONObject().apply {
            put("rut", rutLimpio)
            put("nombre", sNombre)
            put("email", sCorreo)
            put("telefono", sTelefono)
            put("password", sClave)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                try {
                    val estado = response.optString("estado")
                    val message = response.optString("msg", "Sin mensaje")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    if (estado == "1") {
                        // ⭐ AQUÍ ESTÁ EL CAMBIO CLAVE ⭐
                        // Al terminar, cerramos esta pantalla para volver al Dashboard
                        finish()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al leer respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }
}