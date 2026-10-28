package com.example.data.remote

import com.example.data.model.ResourceItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResourceDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "titulo") val titulo: String? = null,
    @Json(name = "descripcion") val descripcion: String? = null,
    @Json(name = "tipo") val tipo: String? = null,
    @Json(name = "enlace") val enlace: String? = null,
    @Json(name = "imagen") val imagen: String? = null,
    @Json(name = "calificacion") val calificacion: Double? = null,
    @Json(name = "totalVotos") val totalVotos: Int? = null
) {
    fun toDomain(): ResourceItem {
        return ResourceItem(
            id = id ?: "",
            titulo = titulo ?: "",
            descripcion = descripcion ?: "",
            tipo = tipo ?: "Libro",
            enlace = enlace ?: "",
            imagen = imagen ?: "",
            calificacion = calificacion ?: 0.0,
            totalVotos = totalVotos ?: 0
        )
    }

    companion object {
        fun fromDomain(domain: ResourceItem): ResourceDto {
            return ResourceDto(
                id = domain.id.ifEmpty { null },
                titulo = domain.titulo,
                descripcion = domain.descripcion,
                tipo = domain.tipo,
                enlace = domain.enlace,
                imagen = domain.imagen,
                calificacion = domain.calificacion,
                totalVotos = domain.totalVotos
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class ResourcesResponseWrapper(
    @Json(name = "recursos") val recursos: List<ResourceDto>? = null
)
