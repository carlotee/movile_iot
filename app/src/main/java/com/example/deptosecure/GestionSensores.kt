package com.example.deptosecure

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest // Usaremos StringRequest para listas simples
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class GestionSensores : AppCompatActivity() {

    private lateinit var edtMac: EditText
    private lateinit var edtTipo: EditText
    private lateinit var btnAgregar: Button
    private lateinit var recycler: RecyclerView

    private val listaSensores = ArrayList<Sensor>()
    private lateinit var adapter: SensorAdapter

    // TU IP (La que vi en tu código anterior)
    private val BASE_URL = "http://3.208.190.223/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        edtMac = findViewById(R.id.edtMac)
        edtTipo = findViewById(R.id.edtTipoSensor)
        btnAgregar = findViewById(R.id.btnAgregarSensor)
        recycler = findViewById(R.id.recyclerSensores)

        recycler.layoutManager = LinearLayoutManager(this)

        // Configuramos el adaptador con las acciones de los botones
        adapter = SensorAdapter(listaSensores,
            onEstadoClick = { sensor -> cambiarEstado(sensor) },
            onEliminarClick = { sensor -> eliminarSensor(sensor) }
        )
        recycler.adapter = adapter

        cargarSensores()

        btnAgregar.setOnClickListener {
            if (edtMac.text.isNotEmpty() && edtTipo.text.isNotEmpty()) {
                agregarSensor()
            } else {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarSensores() {
        val url = BASE_URL + "obtener_sensores.php"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    listaSensores.clear()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val sensor = Sensor(
                            obj.getString("id"),
                            obj.getString("codigo"),
                            obj.getString("tipo"),
                            obj.getString("estado")
                        )
                        listaSensores.add(sensor)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Toast.makeText(this, "Error parseando datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun agregarSensor() {
        val url = BASE_URL + "agregar_sensor.php"
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("codigo", edtMac.text.toString())
        params.put("tipo", edtTipo.text.toString())
        params.put("estado", "ACTIVO") // Por defecto activo

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, response.optString("msg"), Toast.LENGTH_SHORT).show()
                cargarSensores() // Recargar lista
                edtMac.setText("")
                edtTipo.setText("")
            },
            { error ->
                Toast.makeText(this, "Error al agregar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun cambiarEstado(sensor: Sensor) {
        val nuevoEstado = if (sensor.estado == "ACTIVO") "INACTIVO" else "ACTIVO"
        val url = BASE_URL + "editar_sensor_estado.php"
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("id", sensor.id)
        params.put("estado", nuevoEstado)

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show()
                cargarSensores()
            },
            { error ->
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun eliminarSensor(sensor: Sensor) {
        val url = BASE_URL + "eliminar_sensor.php"
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("id", sensor.id)

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, "Sensor eliminado", Toast.LENGTH_SHORT).show()
                cargarSensores()
            },
            { error ->
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }
}