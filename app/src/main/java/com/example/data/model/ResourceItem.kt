package com.example.data.model

data class ResourceItem(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "Libro", // Libro, Video, Artículo, Tutorial
    val enlace: String = "",
    val imagen: String = "",
    val calificacion: Double = 0.0,
    val totalVotos: Int = 0
) {
    val ratingDisplay: String
        get() = String.format("%.1f", calificacion)
}
