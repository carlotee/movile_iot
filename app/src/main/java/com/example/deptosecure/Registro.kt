package com.example.deptosecure

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import org.json.JSONObject

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

        // 👉 Botón registrar con validación simplificada
        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }

        // 👉 Texto ir al login
        txtLogin2.setOnClickListener {
            irAlLogin()
        }
    }

    private fun irAlLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // ⭐ VALIDACIONES (SIN RUT ESTRICTO) ⭐
    private fun validarCampos(): Boolean {
        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()
        val sConfirm = confirmClave.text.toString().trim()

        // 1. Validar Nombre (Solo letras)
        if (sNombre.isEmpty()) {
            nombre.error = "El nombre es obligatorio"
            return false
        }
        if (!sNombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$"))) {
            nombre.error = "El nombre solo puede tener letras"
            return false
        }

        // 2. Validar RUT (SOLO QUE NO ESTÉ VACÍO)
        // Quitamos la validación matemática estricta para probar
        if (sRut.isEmpty()) {
            rut.error = "El RUT es obligatorio"
            return false
        }

        // 3. Validar Correo (Gmail)
        if (sCorreo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(sCorreo).matches()) {
            correo.error = "Correo inválido"
            return false
        }
        if (!sCorreo.endsWith("@gmail.com")) {
            correo.error = "Solo se permite @gmail.com"
            return false
        }

        // 4. Validar Teléfono (9 dígitos)
        if (sTelefono.isEmpty() || !sTelefono.matches(Regex("^9[0-9]{8}$"))) {
            telefono.error = "Teléfono inválido (Ej: 912345678)"
            return false
        }

        // 5. Validar Contraseña
        if (sClave.isEmpty() || sClave.length < 4) {
            clave.error = "Mínimo 4 caracteres"
            return false
        }
        if (sClave != sConfirm) {
            confirmClave.error = "Las contraseñas no coinciden"
            return false
        }

        return true
    }

    private fun registrarUsuario() {
        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()

        // Limpiamos puntos y guiones por seguridad, pero aceptamos cualquier RUT
        val rutLimpio = sRut.replace(".", "").replace("-", "")

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
                    // Leemos respuesta flexible (estado string o int)
                    val estado = response.optString("estado")
                    val message = response.optString("msg", "Sin mensaje")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    if (estado == "1") {
                        irAlLogin()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al leer respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Diagnóstico de error detallado
                val response = error.networkResponse
                if (error is com.android.volley.ParseError) {
                    Toast.makeText(this, "Error de Formato (PHP devolvió HTML o basura)", Toast.LENGTH_LONG).show()
                } else if (response != null && response.data != null) {
                    val errorString = String(response.data)
                    Toast.makeText(this, "Error Servidor (${response.statusCode}): $errorString", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error conexión: ${error.message}", Toast.LENGTH_LONG).show()
                }
                error.printStackTrace()
            }
        )

        queue.add(request)
    }
}