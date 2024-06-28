package kr.ac.kopo.please02

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import kr.ac.kopo.please02.databinding.ActivityDailyExpensesBinding
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyExpensesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnShowExpenses.setOnClickListener {
            val date = selectedDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
            showExpensesForDate(date)
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.time)
                binding.etDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showExpensesForDate(date: String) {
        val expenses = mutableListOf<ExpenseActivity.Expense>()
        val cursor = database.query(
            DatabaseHelper.TABLE_EXPENSES,
            null,
            "${DatabaseHelper.COLUMN_DATE} = ?",
            arrayOf(date),
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

        if (expenses.isEmpty()) {
            Toast.makeText(this, "선택한 날짜에 지출 항목이 없습니다.", Toast.LENGTH_SHORT).show()
        }

        val adapter = ExpenseAdapter(this, expenses) { expense ->
            confirmDelete(expense)
        }
        binding.lvDailyExpenses.adapter = adapter
    }

    private fun confirmDelete(expense: ExpenseActivity.Expense) {
        AlertDialog.Builder(this)
            .setMessage("이 항목을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                deleteExpense(expense.id)
                dialog.dismiss()
                selectedDate?.let { showExpensesForDate(it) }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteExpense(id: Long) {
        database.delete(DatabaseHelper.TABLE_EXPENSES, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id.toString()))
    }
}