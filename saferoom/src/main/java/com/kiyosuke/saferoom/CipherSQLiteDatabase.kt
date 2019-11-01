package com.kiyosuke.saferoom

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteTransactionListener
import android.os.CancellationSignal
import android.util.Pair
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteStatement
import net.sqlcipher.database.SQLiteCursor
import net.sqlcipher.database.SQLiteDatabase
import java.util.*

class CipherSQLiteDatabase(private val delegate: SQLiteDatabase) : SupportSQLiteDatabase {

    override fun setMaximumSize(numBytes: Long): Long = delegate.setMaximumSize(numBytes)

    override fun insert(table: String?, conflictAlgorithm: Int, values: ContentValues?): Long {
        return delegate.insertWithOnConflict(table, null, values, conflictAlgorithm)
    }

    override fun enableWriteAheadLogging(): Boolean = delegate.enableWriteAheadLogging()

    override fun isDatabaseIntegrityOk(): Boolean = delegate.isDatabaseIntegrityOk

    override fun isWriteAheadLoggingEnabled(): Boolean = delegate.isWriteAheadLoggingEnabled

    override fun disableWriteAheadLogging() {
        delegate.disableWriteAheadLogging()
    }

    override fun compileStatement(sql: String?): SupportSQLiteStatement {
        return CipherSQLiteStatement(delegate.compileStatement(sql))
    }

    override fun beginTransactionWithListenerNonExclusive(transactionListener: SQLiteTransactionListener?) {
        delegate.beginTransactionWithListenerNonExclusive(transactionListener?.wrap())
    }

    override fun isDbLockedByCurrentThread(): Boolean = delegate.isDbLockedByCurrentThread

    override fun setPageSize(numBytes: Long) {
        delegate.pageSize = numBytes
    }

    override fun query(query: String): Cursor {
        return query(CipherSQLiteQuery(query))
    }

    override fun query(query: String, bindArgs: Array<out Any>?): Cursor {
        return query(CipherSQLiteQuery(query, bindArgs))
    }

    override fun query(supportQuery: SupportSQLiteQuery): Cursor {
        return query(supportQuery, null)
    }

    override fun query(
        supportQuery: SupportSQLiteQuery,
        cancellationSignal: CancellationSignal?
    ): Cursor {
        return delegate.rawQueryWithFactory({ db, masterQuery, editTable, query ->
            supportQuery.bindTo(CipherSQLiteProgram(query))
            SQLiteCursor(db, masterQuery, editTable, query)
        }, supportQuery.sql, EMPTY_STRING_ARRAY, null)
    }

    override fun endTransaction() {
        delegate.endTransaction()
    }

    override fun getMaximumSize(): Long = delegate.maximumSize

    override fun setLocale(locale: Locale?) {
        delegate.setLocale(locale)
    }

    override fun beginTransaction() {
        delegate.beginTransaction()
    }

    override fun update(
        table: String?,
        conflictAlgorithm: Int,
        values: ContentValues?,
        whereClause: String?,
        whereArgs: Array<out Any>?
    ): Int {
        require(!(values == null || values.size() == 0)) { "Empty values" }

        whereArgs.isNullOrEmpty()
        val sql = StringBuilder(120).apply {
            append("UPDATE ")
            append(CONFLICT_VALUES[conflictAlgorithm])
            append(table)
            append(" SET ")
        }

        val setValuesSize = values.size()
        val bindArgsSize = if (whereArgs == null) setValuesSize else setValuesSize + whereArgs.size
        val bindArgs = arrayOfNulls<Any>(setValuesSize)
        var i = 0
        for (colName in values.keySet()) {
            sql.append(if (i > 0) "," else "")
            sql.append(colName)
            bindArgs[i++] = values.get(colName)
        }
        if (whereArgs != null) {
            i = setValuesSize
            while (i < bindArgsSize) {
                bindArgs[i] = whereArgs[i - setValuesSize]
                i++
            }
        }
        if (!whereClause.isNullOrEmpty()) {
            sql.append(" WHERE ")
            sql.append(whereClause)
        }
        val stmt = compileStatement(sql.toString())
        CipherSQLiteQuery.bind(stmt, bindArgs)
        return stmt.executeUpdateDelete()
    }

    override fun isOpen(): Boolean = delegate.isOpen

    override fun getAttachedDbs(): MutableList<Pair<String, String>> = delegate.attachedDbs

    override fun getVersion(): Int = delegate.version

    override fun execSQL(sql: String?) {
        delegate.execSQL(sql)
    }

    override fun execSQL(sql: String?, bindArgs: Array<out Any>?) {
        delegate.execSQL(sql, bindArgs)
    }

    override fun yieldIfContendedSafely(): Boolean = delegate.yieldIfContendedSafely()

    override fun yieldIfContendedSafely(sleepAfterYieldDelay: Long): Boolean {
        return delegate.yieldIfContendedSafely(sleepAfterYieldDelay)
    }

    override fun close() {
        delegate.close()
    }

    override fun delete(table: String, whereClause: String?, whereArgs: Array<out Any>?): Int {
        val query = "DELETE FROM $table" +
                if (whereClause.isNullOrEmpty()) "" else " WHERE $whereClause"
        val stmt = compileStatement(query)
        CipherSQLiteQuery.bind(stmt, whereArgs)
        return stmt.executeUpdateDelete()
    }

    override fun needUpgrade(newVersion: Int): Boolean = delegate.needUpgrade(newVersion)

    override fun setMaxSqlCacheSize(cacheSize: Int) {
        delegate.maxSqlCacheSize = cacheSize
    }

    override fun setForeignKeyConstraintsEnabled(enable: Boolean) {
        delegate.setForeignKeyConstraintsEnabled(enable)
    }

    override fun beginTransactionNonExclusive() {
        delegate.beginTransactionNonExclusive()
    }

    override fun setTransactionSuccessful() {
        delegate.setTransactionSuccessful()
    }

    override fun setVersion(version: Int) {
        delegate.version = version
    }

    override fun beginTransactionWithListener(transactionListener: SQLiteTransactionListener?) {
        delegate.beginTransactionWithListener(transactionListener?.wrap())
    }

    override fun inTransaction(): Boolean = delegate.inTransaction()

    override fun isReadOnly(): Boolean = delegate.isReadOnly

    override fun getPath(): String = delegate.path

    override fun getPageSize(): Long = delegate.pageSize

    fun isDelegate(sqLiteDatabase: SQLiteDatabase): Boolean = delegate == sqLiteDatabase

    companion object {
        private val CONFLICT_VALUES =
            arrayOf("", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE ")
        private val EMPTY_STRING_ARRAY = emptyArray<String>()
    }
}
