package kr.ac.kopo.please02

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kopo.please02.databinding.ActivityYearlyExpensesBinding
import android.database.sqlite.SQLiteDatabase

class YearlyExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYearlyExpensesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var expenseHelper: ExpenseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYearlyExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase
        expenseHelper = ExpenseHelper(database)

        val selectedYear = intent.getStringExtra("SELECTED_YEAR") ?: ""

        showYearlySummary(selectedYear)
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showYearlySummary(year: String) {
        // Calculate and display total expenses for each month in the year
        val monthlyExpenses = expenseHelper.calculateMonthlyExpensesForYear(year)
        binding.tvYearlyExpenses.text = monthlyExpenses.entries.joinToString("\n") {
            "${it.key}: ${it.value.toInt()}원"
        }
    }
}