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
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Month and Year")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                listener?.invoke(binding.pickerYear.value, binding.pickerMonth.value)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
