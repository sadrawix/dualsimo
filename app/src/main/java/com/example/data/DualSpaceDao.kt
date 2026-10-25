package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DualSpaceDao {
    @Query("SELECT * FROM cloned_apps ORDER BY id ASC")
    fun getAllClonedApps(): Flow<List<ClonedApp>>

    @Query("SELECT * FROM cloned_apps WHERE id = :id LIMIT 1")
    suspend fun getClonedAppById(id: Long): ClonedApp?

    @Query("SELECT COUNT(*) FROM cloned_apps")
    fun getClonedAppsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClonedApp(app: ClonedApp): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<ClonedApp>)

    @Update
    suspend fun updateClonedApp(app: ClonedApp)

    @Delete
    suspend fun deleteClonedApp(app: ClonedApp)

    @Query("DELETE FROM cloned_apps WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE cloned_apps SET cacheSizeMb = 1.2 WHERE id = :id")
    suspend fun clearCache(id: Long)

    @Query("UPDATE cloned_apps SET cacheSizeMb = 1.0")
    suspend fun clearAllCache()
}
