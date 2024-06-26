package kr.ac.kopo.please02

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kopo.please02.databinding.ActivityMonthlyExpensesBinding
import android.database.sqlite.SQLiteDatabase

class MonthlyExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonthlyExpensesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var expenseSummaryHelper: ExpenseSummaryHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase
        expenseSummaryHelper = ExpenseSummaryHelper(database)

        val selectedMonth = intent.getStringExtra("SELECTED_MONTH") ?: ""
        if (selectedMonth.isNotEmpty()) {
            binding.tvMonth.text = selectedMonth
            showExpensesForMonth(selectedMonth)
            showCategoryExpensesForMonth(selectedMonth)
        } else {
            // Handle case when selectedMonth is empty
            binding.tvMonth.text = "No month selected"
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showExpensesForMonth(month: String) {
        val expenses = mutableListOf<ExpenseActivity.Expense>()
        val cursor = database.query(
            DatabaseHelper.TABLE_EXPENSES,
            null,
            "${DatabaseHelper.COLUMN_DATE} LIKE ?",
            arrayOf("$month%"),
            null, null, "${DatabaseHelper.COLUMN_DATE} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val category = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val date = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                expenses.add(ExpenseActivity.Expense(id, title, category, amount, date))
            }
        }
        cursor.close()

        val adapter = ExpenseAdapter(this, expenses)
        binding.lvMonthlyExpenses.adapter = adapter

        // Calculate and display total expenses for the month
        val total = expenseSummaryHelper.calculateTotalExpensesForMonth(month)
        binding.tvTotalExpenses.text = "총금액: $total"

        // Calculate and display total expenses for the previous month
        val previousTotal = expenseSummaryHelper.calculateTotalExpensesForPreviousMonth(month)
        val difference = total - previousTotal
        binding.tvComparison.text = "지난달대비 ${if (difference >= 0) "+" else ""}$difference"
    }

    private fun showCategoryExpensesForMonth(month: String) {
        val categoryExpenses = expenseSummaryHelper.calculateCategoryExpensesForMonth(month)
        val formattedExpenses = categoryExpenses.entries.joinToString("\n") {
            "${it.key}: $${it.value}"
        }
        binding.tvCategoryExpenses.text = formattedExpenses
    }
}
