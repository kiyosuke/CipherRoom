package com.kiyosuke.cipherroom

import android.app.Application
import androidx.room.Room
import com.kiyosuke.cipherroom.db.CacheDatabase
import com.kiyosuke.saferoom.CipherRoomFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val factory = CipherRoomFactory("password")
        db = Room.databaseBuilder(applicationContext, CacheDatabase::class.java, "saferoom.db")
            .openHelperFactory(factory)
            .build()
    }

    companion object {
        lateinit var db: CacheDatabase
    }
}