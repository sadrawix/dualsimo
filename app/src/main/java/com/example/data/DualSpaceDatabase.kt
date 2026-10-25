package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ClonedApp::class], version = 2, exportSchema = false)
abstract class DualSpaceDatabase : RoomDatabase() {
    abstract fun dualSpaceDao(): DualSpaceDao

    companion object {
        @Volatile
        private var INSTANCE: DualSpaceDatabase? = null

        fun getDatabase(context: Context): DualSpaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DualSpaceDatabase::class.java,
                    "dual_space_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
