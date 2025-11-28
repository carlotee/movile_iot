package com.example.deptosecure

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var btnCrear: Button
    private val listaUsuarios = ArrayList<Usuario>()
    private lateinit var adapter: UsuarioAdapter

    // Variable para guardar mi propio ID
    private var miIdPropio: String = ""

    private val URL_BASE = "http://3.208.190.223/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        // ⭐ RECIBIR MI ID DESDE PRINCIPAL
        miIdPropio = intent.getStringExtra("MI_ID") ?: ""

        recycler = findViewById(R.id.recyclerUsuarios)
        btnCrear = findViewById(R.id.btnCrearNuevo)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = UsuarioAdapter(listaUsuarios,
            onEditarClick = { usuario -> mostrarDialogoEditar(usuario) },
            onEliminarClick = { usuario -> confirmarEliminar(usuario) }
        )
        recycler.adapter = adapter

        btnCrear.setOnClickListener {
            startActivity(Intent(this, CrearUsuarioActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        val url = URL_BASE + "obtener_todos_usuarios.php"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    listaUsuarios.clear()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        listaUsuarios.add(Usuario(
                            obj.getString("id_usuario"),
                            obj.getString("rut"),
                            obj.getString("nombre"),
                            obj.getString("email"),
                            obj.getString("telefono"),
                            obj.getString("rol")
                        ))
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error -> Toast.makeText(this, "Error carga: ${error.message}", Toast.LENGTH_SHORT).show() }
        )
        queue.add(request)
    }

    private fun confirmarEliminar(usuario: Usuario) {
        // ⭐ VALIDACIÓN DE SEGURIDAD ⭐
        // Si el ID del usuario que quiero borrar es IGUAL a MI ID, no dejo pasar.
        if (usuario.id == miIdPropio) {
            Toast.makeText(this, "⚠️ No puedes eliminar tu propia cuenta de Administrador.", Toast.LENGTH_LONG).show()
            return // <--- Detiene la función aquí
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de eliminar a ${usuario.nombre}? Se borrarán sus sensores asignados.")
            .setPositiveButton("Sí, eliminar") { _, _ -> eliminarUsuarioAPI(usuario.id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarUsuarioAPI(id: String) {
        val url = URL_BASE + "eliminar_usuario.php"
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put("id_usuario", id)

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, response.optString("msg"), Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            },
            { error -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show() }
        )
        queue.add(request)
    }

    private fun mostrarDialogoEditar(usuario: Usuario) {
        val builder = AlertDialog.Builder(this)

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputNombre = EditText(this)
        inputNombre.setText(usuario.nombre)
        inputNombre.hint = "Nombre"
        layout.addView(inputNombre)

        val inputEmail = EditText(this)
        inputEmail.setText(usuario.email)
        inputEmail.hint = "Email"
        layout.addView(inputEmail)

        val inputRol = EditText(this)
        inputRol.setText(usuario.rol)
        inputRol.hint = "Rol (ADMIN / OPERADOR)"
        layout.addView(inputRol)

        builder.setView(layout)
        builder.setTitle("Editar a ${usuario.nombre}")

        builder.setPositiveButton("Guardar") { _, _ ->
            editarUsuarioAPI(usuario.id, inputNombre.text.toString(), inputEmail.text.toString(), usuario.telefono, inputRol.text.toString())
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun editarUsuarioAPI(id: String, nombre: String, email: String, telefono: String, rol: String) {
        val url = URL_BASE + "editar_usuario.php"
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put("id_usuario", id)
        params.put("nombre", nombre)
        params.put("email", email)
        params.put("telefono", telefono)
        params.put("rol", rol.uppercase())

        val request = JsonObjectRequest(Request.Method.POST, url, params,
            { response ->
                Toast.makeText(this, response.optString("msg"), Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            },
            { error -> Toast.makeText(this, "Error al editar", Toast.LENGTH_SHORT).show() }
        )
        queue.add(request)
    }
}