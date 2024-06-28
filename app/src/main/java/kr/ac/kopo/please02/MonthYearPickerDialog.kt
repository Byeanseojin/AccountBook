package kr.ac.kopo.please02

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import kr.ac.kopo.please02.databinding.DialogMonthYearPickerBinding
import java.util.Calendar

class MonthYearPickerDialog : DialogFragment() {

    private var listener: ((year: Int, month: Int) -> Unit)? = null

    fun setListener(listener: (year: Int, month: Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogMonthYearPickerBinding.inflate(LayoutInflater.from(context))

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        binding.pickerYear.minValue = currentYear - 50
        binding.pickerYear.maxValue = currentYear + 50
        binding.pickerYear.value = currentYear

        binding.pickerMonth.minValue = 0
        binding.pickerMonth.maxValue = 11
        binding.pickerMonth.value = currentMonth

        binding.pickerMonth.displayedValues = arrayOf(
            "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12"
        )

        return AlertDialog.Builder(requireContext())
            .setTitle("연도와 월을 입력하세요")
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                listener?.invoke(binding.pickerYear.value, binding.pickerMonth.value)
            }
            .setNegativeButton("취소", null)
            .create()
    }
}