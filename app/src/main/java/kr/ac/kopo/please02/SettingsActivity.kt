package kr.ac.kopo.please02

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kopo.please02.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE)

        val existingName = sharedPreferences.getString("user_name", "")
        val existingGoalAmount = sharedPreferences.getFloat("goal_amount", 0.0f).toInt()

        binding.etName.setText(existingName)
        if (existingGoalAmount != 0) {
            binding.etGoalAmount.setText(existingGoalAmount.toString())
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val goalAmount = binding.etGoalAmount.text.toString().toDoubleOrNull() ?: 0.0

            with(sharedPreferences.edit()) {
                putString("user_name", name)
                putFloat("goal_amount", goalAmount.toFloat())
                apply()
            }

            Toast.makeText(this, "저장되었습니다!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}