package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ResourceItem

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    val calificacion: Double,
    val totalVotos: Int
) {
    fun toDomain(): ResourceItem = ResourceItem(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        tipo = tipo,
        enlace = enlace,
        imagen = imagen,
        calificacion = calificacion,
        totalVotos = totalVotos
    )

    companion object {
        fun fromDomain(item: ResourceItem): ResourceEntity = ResourceEntity(
            id = item.id,
            titulo = item.titulo,
            descripcion = item.descripcion,
            tipo = item.tipo,
            enlace = item.enlace,
            imagen = item.imagen,
            calificacion = item.calificacion,
            totalVotos = item.totalVotos
        )
    }
}
