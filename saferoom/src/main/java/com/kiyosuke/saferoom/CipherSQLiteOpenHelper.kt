package com.kiyosuke.saferoom

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import net.sqlcipher.DatabaseErrorHandler
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class CipherSQLiteOpenHelper internal constructor(
    context: Context,
    name: String?,
    callback: SupportSQLiteOpenHelper.Callback,
    private val password: String
) : SupportSQLiteOpenHelper {

    private val delegate: OpenHelper

    init {
        SQLiteDatabase.loadLibs(context)
        delegate = createDelegate(context, name, callback)
    }

    private fun createDelegate(
        context: Context,
        name: String?,
        callback: SupportSQLiteOpenHelper.Callback
    ): OpenHelper {
        val dbRef = arrayOfNulls<CipherSQLiteDatabase>(1)
        return OpenHelper(context, name, dbRef, callback)
    }

    override fun getDatabaseName(): String = delegate.databaseName

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        return delegate.getWritableSupportDatabase(password)
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        return delegate.getReadableSupportDatabase(password)
    }

    override fun close() {
        delegate.close()
    }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        delegate.setWriteAheadLoggingEnabled(enabled)
    }

    class OpenHelper(
        context: Context,
        name: String?,
        private val dbRef: Array<CipherSQLiteDatabase?>,
        private val callback: SupportSQLiteOpenHelper.Callback
    ) : SQLiteOpenHelper(
        context,
        name,
        null,
        callback.version,
        null,
        DatabaseErrorHandler { dbObj -> callback.onCorruption(getWrappedDb(dbRef, dbObj)) }) {

        private var migrated: Boolean = false

        override fun onCreate(db: SQLiteDatabase) {
            callback.onCreate(getWrappedDb(db))
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            migrated = true
            callback.onUpgrade(getWrappedDb(db), oldVersion, newVersion)
        }

        override fun onConfigure(db: SQLiteDatabase) {
            callback.onConfigure(getWrappedDb(db))
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            migrated = true
            callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion)
        }

        override fun onOpen(db: SQLiteDatabase) {
            if (!migrated) {
                callback.onOpen(getWrappedDb(db))
            }
        }

        @Synchronized
        override fun close() {
            super.close()
            dbRef[0] = null
        }

        @Synchronized
        fun getWritableSupportDatabase(password: ByteArray): SupportSQLiteDatabase {
            return getWritableSupportDatabase {
                super.getWritableDatabase(password)
            }
        }

        @Synchronized
        fun getWritableSupportDatabase(password: CharArray): SupportSQLiteDatabase {
            return getWritableSupportDatabase {
                super.getWritableDatabase(password)
            }
        }

        @Synchronized
        fun getWritableSupportDatabase(password: String): SupportSQLiteDatabase {
            return getWritableSupportDatabase {
                super.getWritableDatabase(password)
            }
        }

        @Synchronized
        private fun getWritableSupportDatabase(getWritableDatabase: () -> SQLiteDatabase): SupportSQLiteDatabase {
            migrated = false
            val db = getWritableDatabase()
            if (migrated) {
                close()
                return getWritableSupportDatabase(getWritableDatabase)
            }
            return getWrappedDb(db)
        }

        @Synchronized
        fun getReadableSupportDatabase(password: ByteArray): SupportSQLiteDatabase {
            return getReadableSupportDatabase {
                super.getReadableDatabase(password)
            }
        }

        @Synchronized
        fun getReadableSupportDatabase(password: CharArray): SupportSQLiteDatabase {
            return getReadableSupportDatabase {
                super.getReadableDatabase(password)
            }
        }

        @Synchronized
        fun getReadableSupportDatabase(password: String): SupportSQLiteDatabase {
            return getReadableSupportDatabase {
                super.getReadableDatabase(password)
            }
        }

        @Synchronized
        private fun getReadableSupportDatabase(getReadableDatabase: () -> SQLiteDatabase): SupportSQLiteDatabase {
            migrated = false
            val db = getReadableDatabase()
            if (migrated) {
                close()
                return getWritableSupportDatabase(getReadableDatabase)
            }
            return getWrappedDb(db)
        }

        internal fun getWrappedDb(sqLiteDatabase: SQLiteDatabase): CipherSQLiteDatabase {
            return getWrappedDb(dbRef, sqLiteDatabase)
        }

        companion object {

            internal fun getWrappedDb(
                refHolder: Array<CipherSQLiteDatabase?>,
                sqLiteDatabase: SQLiteDatabase
            ): CipherSQLiteDatabase {
                val dbRef = refHolder[0]
                if (dbRef == null || dbRef.isDelegate(sqLiteDatabase)) {
                    refHolder[0] = CipherSQLiteDatabase(sqLiteDatabase)
                }
                return requireNotNull(refHolder[0])
            }
        }
    }
}