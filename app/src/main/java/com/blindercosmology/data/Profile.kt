package com.blindercosmology.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val utcOffsetHours: Double,
    val latitude: Double,
    val longitude: Double,
    val placeLabel: String,
    val isMainUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
