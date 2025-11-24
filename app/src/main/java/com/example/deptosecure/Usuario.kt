package com.example.deptosecure

data class Usuario(val id: String, val nombre: String) {
    // Sobreescribimos toString para que el Spinner muestre solo el nombre
    override fun toString(): String {
        return nombre
    }
}