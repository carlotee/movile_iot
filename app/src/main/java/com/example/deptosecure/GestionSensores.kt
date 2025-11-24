package com.example.deptosecure

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class GestionSensores : AppCompatActivity() {

    private lateinit var edtMac: EditText
    private lateinit var edtTipo: EditText
    private lateinit var btnAgregar: Button
    private lateinit var recycler: RecyclerView

    // Variables para el Spinner de usuarios
    private lateinit var spinnerUsuarios: Spinner
    private val listaUsuarios = ArrayList<Usuario>()

    private val listaSensores = ArrayList<Sensor>()
    private lateinit var adapter: SensorAdapter

    // TU IP
    private val BASE_URL = "http://3.208.190.223/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        // Vincular vistas
        edtMac = findViewById(R.id.edtMac)
        edtTipo = findViewById(R.id.edtTipoSensor)
        btnAgregar = findViewById(R.id.btnAgregarSensor)
        recycler = findViewById(R.id.recyclerSensores)
        spinnerUsuarios = findViewById(R.id.spinnerUsuarios)

        recycler.layoutManager = LinearLayoutManager(this)

        // Configuramos el adaptador
        adapter = SensorAdapter(listaSensores,
            onEstadoClick = { sensor -> cambiarEstado(sensor) },
            onEliminarClick = { sensor -> eliminarSensor(sensor) }
        )
        recycler.adapter = adapter

        // Cargar datos iniciales
        cargarSensores()
        cargarUsuarios()

        // Botón Agregar
        btnAgregar.setOnClickListener {
            agregarSensor()
        }
    }

    private fun cargarUsuarios() {
        val url = BASE_URL + "obtener_usuarios.php"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    listaUsuarios.clear()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        listaUsuarios.add(Usuario(obj.getString("id"), obj.getString("nombre")))
                    }

                    val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaUsuarios)
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerUsuarios.adapter = adapterSpinner

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión usuarios", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
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

                        // ⭐ CORRECCIÓN APLICADA: Leemos el usuario del JSON
                        val nombreUsuario = obj.optString("usuario", "Sin Asignar")

                        val sensor = Sensor(
                            obj.getString("id"),
                            obj.getString("codigo"),
                            obj.getString("tipo"),
                            obj.getString("estado"),
                            nombreUsuario // ⭐ Pasamos el 5to parámetro necesario
                        )
                        listaSensores.add(sensor)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun agregarSensor() {
        val usuarioSeleccionado = spinnerUsuarios.selectedItem as? Usuario

        if (edtMac.text.isEmpty() || edtTipo.text.isEmpty()) {
            Toast.makeText(this, "Complete código y tipo", Toast.LENGTH_SHORT).show()
            return
        }

        if (usuarioSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val url = BASE_URL + "agregar_sensor.php"
        val queue = Volley.newRequestQueue(this)

        val params = JSONObject()
        params.put("codigo", edtMac.text.toString())
        params.put("tipo", edtTipo.text.toString())
        params.put("estado", "ACTIVO")
        params.put("id_usuario", usuarioSeleccionado.id)

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                val msg = response.optString("msg")
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

                if (response.optString("estado") == "1") {
                    cargarSensores()
                    edtMac.setText("")
                    edtTipo.setText("")
                }
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