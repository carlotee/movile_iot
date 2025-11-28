package com.example.deptosecure

data class Usuario(
    val id: String,
    val rut: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val rol: String
) {
    // Sobreescribimos toString para que los Spinners muestren solo el nombre
    override fun toString(): String {
        return nombre
    }
}