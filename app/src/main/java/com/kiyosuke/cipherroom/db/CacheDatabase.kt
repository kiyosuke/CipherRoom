package com.kiyosuke.cipherroom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiyosuke.cipherroom.db.dao.UserDao
import com.kiyosuke.cipherroom.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}