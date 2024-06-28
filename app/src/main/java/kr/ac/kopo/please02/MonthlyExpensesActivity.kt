package kr.ac.kopo.please02

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kopo.please02.databinding.ActivityMonthlyExpensesBinding
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AlertDialog

class MonthlyExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonthlyExpensesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var expenseHelper: ExpenseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase
        expenseHelper = ExpenseHelper(database)

        val selectedMonth = intent.getStringExtra("SELECTED_MONTH") ?: ""
        if (selectedMonth.isNotEmpty()) {
            binding.tvMonth.text = selectedMonth
            showExpensesForMonth(selectedMonth)
            showCategoryExpensesForMonth(selectedMonth)
        } else {
            binding.tvMonth.text = "달이 선택되지 않았습니다"
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

        val adapter = ExpenseAdapter(this, expenses) { expense ->
            confirmDelete(expense)
        }
        binding.lvMonthlyExpenses.adapter = adapter

        val total = expenseHelper.calculateTotalExpensesForMonth(month).toInt()
        binding.tvTotalExpenses.text = "총금액: $total"+"원"

        val previousTotal = expenseHelper.calculateTotalExpensesForPreviousMonth(month).toInt()
        val difference = total - previousTotal
        binding.tvComparison.text = "지난달대비 ${if (difference >= 0) "+" else ""}$difference"+"원"
    }

    private fun confirmDelete(expense: ExpenseActivity.Expense) {
        AlertDialog.Builder(this)
            .setMessage("이 항목을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                deleteExpense(expense.id)
                dialog.dismiss()
                val selectedMonth = intent.getStringExtra("SELECTED_MONTH") ?: ""
                if (selectedMonth.isNotEmpty()) {
                    showExpensesForMonth(selectedMonth)
                    showCategoryExpensesForMonth(selectedMonth)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteExpense(id: Long) {
        database.delete(DatabaseHelper.TABLE_EXPENSES, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id.toString()))
    }

    private fun showCategoryExpensesForMonth(month: String) {
        val categoryExpenses = expenseHelper.calculateCategoryExpensesForMonth(month)
        val formattedExpenses = categoryExpenses.entries.joinToString("\n") {
            "${it.key}: ${it.value.toInt()}"+"원"
        }
        binding.tvCategoryExpenses.text = formattedExpenses
    }
}