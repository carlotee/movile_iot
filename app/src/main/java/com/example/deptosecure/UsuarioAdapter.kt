package com.example.deptosecure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuarioAdapter(
    private val lista: ArrayList<Usuario>,
    private val onEditarClick: (Usuario) -> Unit,
    private val onEliminarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txtNombreUsu)
        val rut: TextView = view.findViewById(R.id.txtRutUsu)
        val rol: TextView = view.findViewById(R.id.txtRolUsu)
        val btnEditar: Button = view.findViewById(R.id.btnEditarUsu)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarUsu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_lista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nombre.text = item.nombre
        holder.rut.text = "RUT: ${item.rut}"
        holder.rol.text = "Rol: ${item.rol} | Tel: ${item.telefono}"

        holder.btnEditar.setOnClickListener { onEditarClick(item) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(item) }
    }

    override fun getItemCount() = lista.size
}