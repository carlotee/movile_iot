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


lateinit var usu: EditText
lateinit var clave: EditText
lateinit var btn: Button
lateinit var datos: RequestQueue


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usu=findViewById(R.id.usuario)
        clave=findViewById(R.id.clave)
        btn=findViewById(R.id.btningresar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)

        btnIrRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        datos= Volley.newRequestQueue(this);

        btn.setOnClickListener()
        {
            consultarDatos(usu.getText().toString(),clave.getText().toString());
        }

    }
    fun consultarDatos(usu: String, pass: String) {
        val url = "http://3.208.190.223/apiconsultausu.php?usu=$usu&pass=$pass"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val estado = response.getString("estado")
                    if (estado == "0") {
                        Toast.makeText(this@MainActivity, "Usuario no existe", Toast.LENGTH_LONG).show()
                    } else {
                        val ventana = Intent(this@MainActivity,Principal::class.java
                        )
                        startActivity(ventana)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        datos.add(request)
    }

}