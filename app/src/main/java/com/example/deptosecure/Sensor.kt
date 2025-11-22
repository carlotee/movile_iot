package com.example.deptosecure

data class Sensor(
    val id: String,
    val codigo: String, // La MAC o UID
    val tipo: String,   // Tarjeta o Llavero
    val estado: String  // ACTIVO, INACTIVO, etc.
)