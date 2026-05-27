package com.blindercosmology.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE isMainUser = 1 LIMIT 1")
    fun observeMainUser(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE isMainUser = 1 LIMIT 1")
    suspend fun getMainUser(): ProfileEntity?

    @Query("SELECT * FROM profiles ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity): Long

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("UPDATE profiles SET isMainUser = 0")
    suspend fun clearMainUserFlag()

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun delete(id: Long)
}
