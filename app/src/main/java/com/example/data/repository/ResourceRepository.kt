package com.example.data.repository

import com.example.data.model.LocalizedException
import com.example.R
import android.content.Context
import android.util.Log
import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.ResourceDao
import com.example.data.local.RatingDao
import com.example.data.local.RatingEntity
import com.example.data.local.ResourceEntity
import com.example.data.model.ResourceItem
import com.example.data.remote.ApiClient
import com.example.data.remote.ResourceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException

class ResourceRepository(
    private val context: Context,
    private val resourceDao: ResourceDao,
    private val favoriteDao: FavoriteDao,
    private val ratingDao: RatingDao
) {
    private val TAG = "ResourceRepository"
    private val prefs = context.getSharedPreferences("aprende_settings", Context.MODE_PRIVATE)

    private val _apiUrl = MutableStateFlow(
        prefs.getString("custom_api_url", ApiClient.DEFAULT_API_URL) ?: ApiClient.DEFAULT_API_URL
    )
    val apiUrl: StateFlow<String> = _apiUrl.asStateFlow()

    private val _resources = MutableStateFlow<List<ResourceItem>>(emptyList())
    val resources: StateFlow<List<ResourceItem>> = _resources.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    suspend fun setApiUrl(newUrl: String) {
        val trimmed = newUrl.trim()
        _apiUrl.value = trimmed
        prefs.edit().putString("custom_api_url", trimmed).apply()
        // Refresh with new URL
        loadResources(forceRefresh = true)
    }

    suspend fun resetApiUrl() {
        setApiUrl(ApiClient.DEFAULT_API_URL)
    }

    suspend fun testApiConnection(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val service = ApiClient.getService(url)
            val response = service.getResources()
            response.isSuccessful
        } catch (e: Exception) {
            Log.w(TAG, "Connection test failed: ${e.message}")
            false
        }
    }

    suspend fun loadResources(forceRefresh: Boolean = false): Result<List<ResourceItem>> = withContext(Dispatchers.IO) {
        // Muestra primero la caché local (solo lectura) mientras llega la API
        val localItems = resourceDao.getAllResources().map { it.toDomain() }
        if (localItems.isNotEmpty() && !forceRefresh) {
            _resources.value = localItems
        }

        try {
            val response = ApiClient.getService(_apiUrl.value).getResources()
            if (response.isSuccessful && response.body() != null) {
                var remoteList = response.body()!!.map { it.toDomain() }
                if (remoteList.isEmpty()) {
                    // La API está vacía: se cargan los recursos iniciales EN la API (POST)
                    remoteList = seedRemoteData()
                }
                // La API es la fuente de verdad: la caché se reemplaza por completo
                resourceDao.clearAll()
                resourceDao.insertResources(remoteList.map { ResourceEntity.fromDomain(it) })
                _resources.value = remoteList
                _isOfflineMode.value = false
                Result.success(remoteList)
            } else {
                Log.w(TAG, "API responded with error code: ${response.code()}")
                _isOfflineMode.value = true
                _resources.value = localItems
                Result.failure(IOException("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Remote fetch failed, using local cache: ${e.message}")
            _isOfflineMode.value = true
            _resources.value = localItems
            Result.failure(e)
        }
    }

    suspend fun addResource(item: ResourceItem): Result<ResourceItem> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.getService(_apiUrl.value).createResource(ResourceDto.fromDomain(item))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                return@withContext Result.failure(IOException("HTTP ${response.code()}"))
            }
            val created = body.toDomain()
            resourceDao.insertResource(ResourceEntity.fromDomain(created))
            _resources.value = listOf(created) + _resources.value.filter { it.id != created.id }
            Result.success(created)
        } catch (e: Exception) {
            Log.w(TAG, "POST failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateResource(item: ResourceItem): Result<ResourceItem> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.getService(_apiUrl.value).updateResource(item.id, ResourceDto.fromDomain(item))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                return@withContext Result.failure(IOException("HTTP ${response.code()}"))
            }
            val updated = body.toDomain()
            resourceDao.insertResource(ResourceEntity.fromDomain(updated))
            _resources.value = _resources.value.map { if (it.id == updated.id) updated else it }
            Result.success(updated)
        } catch (e: Exception) {
            Log.w(TAG, "PUT failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteResource(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.getService(_apiUrl.value).deleteResource(id)
            // 404 = ya no existe en el servidor; se limpia igualmente en local
            if (!response.isSuccessful && response.code() != 404) {
                return@withContext Result.failure(IOException("HTTP ${response.code()}"))
            }
            resourceDao.deleteResourceById(id)
            _resources.value = _resources.value.filter { it.id != id }
            Result.success(true)
        } catch (e: Exception) {
            Log.w(TAG, "DELETE failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Un voto por usuario y recurso. Si el usuario ya había votado, su voto anterior se
     * reemplaza (el total de votos no cambia); si no, se suma un voto nuevo.
     * El nuevo promedio se envía a la API con PUT y solo se guarda el voto si la API respondió bien.
     */
    suspend fun rateResource(userId: String, id: String, newRating: Int): Result<ResourceItem> = withContext(Dispatchers.IO) {
        val current = _resources.value.find { it.id == id }
            ?: resourceDao.getResourceById(id)?.toDomain()
            ?: return@withContext Result.failure(LocalizedException(R.string.err_resource_not_found))

        val previous = ratingDao.getRating(userId, id)
        val updated = if (previous == null) {
            val votes = current.totalVotos + 1
            current.copy(
                calificacion = ((current.calificacion * current.totalVotos) + newRating) / votes,
                totalVotos = votes
            )
        } else {
            val votes = current.totalVotos.coerceAtLeast(1)
            current.copy(
                calificacion = ((current.calificacion * votes) - previous.stars + newRating) / votes,
                totalVotos = votes
            )
        }

        val result = updateResource(updated)
        if (result.isSuccess) {
            ratingDao.upsertRating(RatingEntity(userId = userId, resourceId = id, stars = newRating))
        }
        result
    }

    suspend fun getUserRating(userId: String, resourceId: String): Int? = withContext(Dispatchers.IO) {
        ratingDao.getRating(userId, resourceId)?.stars
    }

    // Favorite management
    fun getFavoriteIdsFlow(userId: String): Flow<List<String>> =
        favoriteDao.getFavoriteResourceIds(userId)

    suspend fun toggleFavorite(userId: String, resourceId: String): Boolean = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(userId, resourceId)
        if (isFav) {
            favoriteDao.removeFavorite(userId, resourceId)
            false
        } else {
            favoriteDao.addFavorite(FavoriteEntity(userId = userId, resourceId = resourceId))
            true
        }
    }

    private suspend fun seedRemoteData(): List<ResourceItem> {
        val defaultItems = listOf(
            ResourceItem(
                id = "1",
                titulo = "Clean Architecture: A Craftsman\'s Guide",
                descripcion = "Guía fundamental de Robert C. Martin sobre principios de arquitectura de software desacoplada, testeable y mantenible para sistemas empresariales.",
                tipo = "Libro",
                enlace = "https://www.oreilly.com/library/view/clean-architecture-a/9780134494272/",
                imagen = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.8,
                totalVotos = 18
            ),
            ResourceItem(
                id = "2",
                titulo = "Patrones de Diseño de Software en Kotlin",
                descripcion = "Tutorial en profundidad sobre Creational, Structural y Behavioral patterns implementados con las mejores prácticas y construcciones idiomáticas de Kotlin.",
                tipo = "Tutorial",
                enlace = "https://refactoring.guru/design-patterns/kotlin",
                imagen = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.7,
                totalVotos = 12
            ),
            ResourceItem(
                id = "3",
                titulo = "Curso Completo de Microservicios con Docker y Kubernetes",
                descripcion = "Video interactivo sobre diseño de arquitecturas distribuidas, balanceo de carga, orquestación de contenedores y resiliencia en sistemas cloud.",
                tipo = "Video",
                enlace = "https://www.youtube.com/watch?v=1oxFflxGq_g",
                imagen = "https://images.unsplash.com/photo-1618401471353-b98aedd04e11?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.9,
                totalVotos = 25
            ),
            ResourceItem(
                id = "4",
                titulo = "Optimización de Consultas en Bases de Datos SQL y NoSQL",
                descripcion = "Artículo técnico de ingeniería que explica planes de ejecución, indexación avanzada B-Tree y estrategias de particionamiento sharding.",
                tipo = "Artículo",
                enlace = "https://use-the-index-luke.com/",
                imagen = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.6,
                totalVotos = 14
            ),
            ResourceItem(
                id = "5",
                titulo = "Jetpack Compose: Guía de Arquitectura para Android",
                descripcion = "Documentación oficial y tutorial práctico para dominar el State Hoisting, Recomposición eficiente y flujos unidireccionales (UDF).",
                tipo = "Tutorial",
                enlace = "https://developer.android.com/jetpack/compose/architecture",
                imagen = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.9,
                totalVotos = 30
            ),
            ResourceItem(
                id = "6",
                titulo = "Ciberseguridad Aplicada y OWASP Top 10",
                descripcion = "Libro de referencia sobre mitigación de vulnerabilidades web y móviles, inyecciones, autenticación rota y criptografía moderna.",
                tipo = "Libro",
                enlace = "https://owasp.org/www-project-top-ten/",
                imagen = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=600&auto=format&fit=crop&q=80",
                calificacion = 4.5,
                totalVotos = 16
            )
        )

        val service = ApiClient.getService(_apiUrl.value)
        val created = mutableListOf<ResourceItem>()
        for (item in defaultItems) {
            // El id lo asigna la API; no se envía el id local
            val response = service.createResource(ResourceDto.fromDomain(item.copy(id = "")))
            response.body()?.let { created.add(it.toDomain()) }
        }
        return created
    }
}
