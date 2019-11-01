package com.kiyosuke.saferoom

import net.sqlcipher.database.SQLiteTransactionListener

internal class CipherSQLiteTransactionListener(private val delegate: android.database.sqlite.SQLiteTransactionListener) :
    SQLiteTransactionListener {

    override fun onCommit() {
        delegate.onCommit()
    }

    override fun onRollback() {
        delegate.onRollback()
    }

    override fun onBegin() {
        delegate.onBegin()
    }
}

internal fun android.database.sqlite.SQLiteTransactionListener.wrap(): CipherSQLiteTransactionListener {
    return CipherSQLiteTransactionListener(this)
}