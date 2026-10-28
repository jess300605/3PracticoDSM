package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT resourceId FROM favorites WHERE userId = :userId")
    fun getFavoriteResourceIds(userId: String): Flow<List<String>>

    @Query("SELECT resourceId FROM favorites WHERE userId = :userId")
    suspend fun getFavoriteIdsSync(userId: String): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND resourceId = :resourceId)")
    suspend fun isFavorite(userId: String, resourceId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND resourceId = :resourceId")
    suspend fun removeFavorite(userId: String, resourceId: String)
}

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources")
    fun getAllResourcesFlow(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources")
    suspend fun getAllResources(): List<ResourceEntity>

    @Query("SELECT * FROM resources WHERE id = :id LIMIT 1")
    suspend fun getResourceById(id: String): ResourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<ResourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: ResourceEntity)

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun deleteResourceById(id: String)

    @Query("DELETE FROM resources")
    suspend fun clearAll()
}
