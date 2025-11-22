package com.example.deptosecure

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class HistorialActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var btnRefrescar: Button
    private val listaEventos = ArrayList<Evento>()
    private lateinit var adapter: EventoAdapter

    // URL de tu backend
    private val URL_API = "http://3.208.190.223/obtener_historial.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recycler = findViewById(R.id.recyclerHistorial)
        btnRefrescar = findViewById(R.id.btnRefrescar)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = EventoAdapter(listaEventos)
        recycler.adapter = adapter

        cargarHistorial()

        btnRefrescar.setOnClickListener {
            cargarHistorial()
        }
    }

    private fun cargarHistorial() {
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, URL_API,
            { response ->
                try {
                    listaEventos.clear()
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val evento = Evento(
                            obj.getString("id"),
                            obj.getString("tipo"),
                            obj.getString("fecha"),
                            obj.getString("resultado")
                        )
                        listaEventos.add(evento)
                    }
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Historial actualizado", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }
}