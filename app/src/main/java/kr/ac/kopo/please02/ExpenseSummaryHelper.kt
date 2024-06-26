package kr.ac.kopo.please02

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseSummaryHelper(private val database: SQLiteDatabase) {

    fun calculateTotalExpensesForMonth(month: String): Double {
        var total = 0.0
        val cursor = database.query(
            DatabaseHelper.TABLE_EXPENSES,
            arrayOf("SUM(${DatabaseHelper.COLUMN_AMOUNT}) AS total"),
            "${DatabaseHelper.COLUMN_DATE} LIKE ?",
            arrayOf("$month%"),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        Log.d("ExpenseSummaryHelper", "Total expenses for month $month: $total")
        return total
    }

    fun calculateTotalExpensesForPreviousMonth(month: String): Double {
        if (month.isEmpty()) return 0.0
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        cal.time = dateFormat.parse(month) ?: return 0.0
        cal.add(Calendar.MONTH, -1)
        val previousMonth = dateFormat.format(cal.time)
        return calculateTotalExpensesForMonth(previousMonth)
    }

    fun calculateMonthlyExpensesForYear(year: String): Map<String, Double> {
        val monthlyExpenses = mutableMapOf<String, Double>()
        for (month in 1..12) {
            val formattedMonth = String.format("%s-%02d", year, month)
            monthlyExpenses[formattedMonth] = calculateTotalExpensesForMonth(formattedMonth)
            Log.d("ExpenseSummaryHelper", "Monthly expenses for $formattedMonth: ${monthlyExpenses[formattedMonth]}")
        }
        return monthlyExpenses
    }

    fun calculateCategoryExpensesForMonth(month: String): Map<String, Double> {
        val categoryExpenses = mutableMapOf<String, Double>()
        val cursor = database.query(
            DatabaseHelper.TABLE_EXPENSES,
            arrayOf(DatabaseHelper.COLUMN_CATEGORY, "SUM(${DatabaseHelper.COLUMN_AMOUNT}) AS total"),
            "${DatabaseHelper.COLUMN_DATE} LIKE ?",
            arrayOf("$month%"),
            DatabaseHelper.COLUMN_CATEGORY,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val category = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))
                val total = getDouble(getColumnIndexOrThrow("total"))
                categoryExpenses[category] = total
                Log.d("ExpenseSummaryHelper", "Category expenses for $category in month $month: $total")
            }
        }
        cursor.close()
        return categoryExpenses
    }
}
