package com.example.deptosecure

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventoAdapter(private val lista: ArrayList<Evento>) : RecyclerView.Adapter<EventoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.txtTipoEvento)
        val fecha: TextView = view.findViewById(R.id.txtFecha)
        val resultado: TextView = view.findViewById(R.id.txtResultado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tipo.text = item.tipo
        holder.fecha.text = item.fecha
        holder.resultado.text = item.resultado

        // Lógica visual: Rojo si es DENEGADO, Verde si es PERMITIDO
        if (item.resultado == "DENEGADO") {
            holder.resultado.setTextColor(Color.RED)
        } else {
            holder.resultado.setTextColor(Color.parseColor("#4CAF50")) // Verde
        }
    }

    override fun getItemCount() = lista.size
}