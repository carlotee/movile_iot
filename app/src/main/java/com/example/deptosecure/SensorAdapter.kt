package com.example.deptosecure

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SensorAdapter(
    private val lista: ArrayList<Sensor>,
    private val onEstadoClick: (Sensor) -> Unit,
    private val onEliminarClick: (Sensor) -> Unit
) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val codigo: TextView = view.findViewById(R.id.txtCodigo)
        val tipo: TextView = view.findViewById(R.id.txtTipo)
        val estado: TextView = view.findViewById(R.id.txtEstado)
        val btnEstado: Button = view.findViewById(R.id.btnCambiarEstado)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.codigo.text = "MAC: ${item.codigo}"
        holder.tipo.text = "Tipo: ${item.tipo}"
        holder.estado.text = "Estado: ${item.estado}"

        // Color según estado
        if (item.estado == "ACTIVO") {
            holder.estado.setTextColor(Color.parseColor("#4CAF50")) // Verde
            holder.btnEstado.text = "Desactivar"
        } else {
            holder.estado.setTextColor(Color.parseColor("#F44336")) // Rojo
            holder.btnEstado.text = "Activar"
        }

        holder.btnEstado.setOnClickListener { onEstadoClick(item) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(item) }
    }

    override fun getItemCount() = lista.size
}