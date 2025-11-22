package com.example.deptosecure

data class Evento(
    val id: String,
    val tipo: String,       // Ej: APP_ABRIR, TARJETA_OK
    val fecha: String,      // Ej: 2024-11-20 15:30:00
    val resultado: String   // Ej: PERMITIDO, DENEGADO
)