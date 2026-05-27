package com.blindercosmology.data

import com.blindercosmology.astro.BirthInfo
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: ProfileDao) {

    val mainUser: Flow<ProfileEntity?> = dao.observeMainUser()

    suspend fun saveMainUser(name: String, info: BirthInfo) {
        dao.clearMainUserFlag()
        dao.insert(
            ProfileEntity(
                fullName = name,
                year = info.year, month = info.month, day = info.day,
                hour = info.hour, minute = info.minute,
                utcOffsetHours = info.utcOffsetHours,
                latitude = info.latitude, longitude = info.longitude,
                placeLabel = info.placeLabel,
                isMainUser = true,
            )
        )
    }
}

fun ProfileEntity.toBirthInfo(): BirthInfo = BirthInfo(
    year = year, month = month, day = day,
    hour = hour, minute = minute,
    utcOffsetHours = utcOffsetHours,
    latitude = latitude, longitude = longitude,
    placeLabel = placeLabel,
)
