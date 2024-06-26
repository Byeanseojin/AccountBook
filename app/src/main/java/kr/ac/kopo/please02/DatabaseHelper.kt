package kr.ac.kopo.please02

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_EXPENSES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_TITLE TEXT")
        }
    }

    companion object {
        private const val DATABASE_NAME = "expenses.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DATE = "date"

        private const val CREATE_TABLE_EXPENSES = (
                "CREATE TABLE $TABLE_EXPENSES (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_TITLE TEXT, " +
                        "$COLUMN_CATEGORY TEXT, " +
                        "$COLUMN_AMOUNT REAL, " +
                        "$COLUMN_DATE TEXT" +
                        ")"
                )
    }

    fun deleteExpense(id: Long): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_EXPENSES, "$COLUMN_ID=?", arrayOf(id.toString())) > 0
    }
}
