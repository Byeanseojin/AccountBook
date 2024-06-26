package kr.ac.kopo.please02

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kopo.please02.databinding.ActivityExpenseBinding
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private val limit = 50000 // Example limit
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase

        setupCategorySpinner()

        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnAddExpense.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()
            val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val date = selectedDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_TITLE, title)
                put(DatabaseHelper.COLUMN_CATEGORY, category)
                put(DatabaseHelper.COLUMN_AMOUNT, amount)
                put(DatabaseHelper.COLUMN_DATE, date)
            }

            val newRowId = database.insert(DatabaseHelper.TABLE_EXPENSES, null, values)
            if (newRowId != -1L) {
                Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
                updateExpenseList()
            } else {
                Toast.makeText(this, "Error adding expense", Toast.LENGTH_SHORT).show()
            }
        }

        updateExpenseList()
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategory.adapter = adapter
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

    private fun updateExpenseList() {
        val expenses = mutableListOf<Expense>()
        val cursor = database.query(
            DatabaseHelper.TABLE_EXPENSES,
            null, null, null, null, null, "${DatabaseHelper.COLUMN_DATE} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val category = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val date = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                expenses.add(Expense(id, title, category, amount, date))
            }
        }
        cursor.close()

        val adapter = ExpenseAdapter(this, expenses)
        binding.lvExpenses.adapter = adapter
    }

    data class Expense(val id: Long, val title: String, val category: String, val amount: Double, val date: String)
}
