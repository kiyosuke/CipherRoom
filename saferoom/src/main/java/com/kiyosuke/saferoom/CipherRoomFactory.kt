package com.kiyosuke.saferoom

import androidx.sqlite.db.SupportSQLiteOpenHelper

class CipherRoomFactory(private val password: String) : SupportSQLiteOpenHelper.Factory {

    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        return CipherSQLiteOpenHelper(
            configuration.context,
            configuration.name,
            configuration.callback,
            password
        )
    }
}