package com.kiyosuke.saferoom

import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery

class CipherSQLiteQuery(private val query: String, private val bindArgs: Array<out Any?>? = null) :
    SupportSQLiteQuery {

    override fun bindTo(statement: SupportSQLiteProgram) {
        bind(statement, bindArgs)
    }

    override fun getArgCount(): Int {
        return bindArgs?.size ?: 0
    }

    override fun getSql(): String = query

    companion object {
        @JvmStatic
        fun bind(statement: SupportSQLiteProgram, bindArgs: Array<out Any?>?) {
            if (bindArgs == null) return
            bindArgs.forEachIndexed { index, any ->
                bind(statement, index + 1, any)
            }
        }

        @JvmStatic
        private fun bind(statement: SupportSQLiteProgram, index: Int, arg: Any?) {
            when (arg) {
                null -> statement.bindNull(index)
                is ByteArray -> statement.bindBlob(index, arg)
                is Float -> statement.bindDouble(index, arg.toDouble())
                is Double -> statement.bindDouble(index, arg)
                is Long -> statement.bindLong(index, arg)
                is Int -> statement.bindLong(index, arg.toLong())
                is Short -> statement.bindLong(index, arg.toLong())
                is Byte -> statement.bindLong(index, arg.toLong())
                is String -> statement.bindString(index, arg)
                is Boolean -> statement.bindLong(index, if (arg) 1 else 0)
                else -> throw IllegalArgumentException(
                    "Cannot bind $arg at index $index" +
                            " Supported types: null, byte[], float, double, long, int, short, byte," +
                            " string"
                )
            }
        }
    }
}