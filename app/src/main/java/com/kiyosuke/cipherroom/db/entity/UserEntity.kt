package com.kiyosuke.cipherroom.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int
)