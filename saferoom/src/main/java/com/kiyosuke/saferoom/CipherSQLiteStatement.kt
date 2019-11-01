package com.kiyosuke.saferoom

import androidx.sqlite.db.SupportSQLiteStatement
import net.sqlcipher.database.SQLiteStatement

class CipherSQLiteStatement(private val delegate: SQLiteStatement) :
    CipherSQLiteProgram(delegate),
    SupportSQLiteStatement {

    override fun simpleQueryForLong(): Long = delegate.simpleQueryForLong()

    override fun simpleQueryForString(): String = delegate.simpleQueryForString()

    override fun execute() {
        delegate.execute()
    }

    override fun executeInsert(): Long = delegate.executeInsert()

    override fun executeUpdateDelete(): Int = delegate.executeUpdateDelete()
}