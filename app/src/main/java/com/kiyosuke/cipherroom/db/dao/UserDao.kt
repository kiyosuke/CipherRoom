package com.kiyosuke.cipherroom.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiyosuke.cipherroom.db.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<UserEntity>)

    @Query("SELECT * FROM t_users")
    fun allUsers(): List<UserEntity>

    @Query("DELETE FROM t_users WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM t_users")
    fun deleteAll()
}