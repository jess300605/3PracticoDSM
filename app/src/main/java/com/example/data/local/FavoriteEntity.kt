package com.example.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "resourceId"],
    indices = [Index(value = ["userId"]), Index(value = ["resourceId"])]
)
data class FavoriteEntity(
    val userId: String,
    val resourceId: String,
    val addedAt: Long = System.currentTimeMillis()
)
