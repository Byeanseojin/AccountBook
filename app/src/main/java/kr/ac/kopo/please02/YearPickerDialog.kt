package kr.ac.kopo.please02

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import kr.ac.kopo.please02.databinding.DialogYearPickerBinding
import java.util.Calendar

class YearPickerDialog : DialogFragment() {

    private var listener: ((year: Int) -> Unit)? = null

    fun setListener(listener: (year: Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogYearPickerBinding.inflate(layoutInflater)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        binding.pickerYear.minValue = currentYear - 50
        binding.pickerYear.maxValue = currentYear + 50
        binding.pickerYear.value = currentYear

        return AlertDialog.Builder(requireContext())
            .setTitle("연도 선택")
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                listener?.invoke(binding.pickerYear.value)
            }
            .setNegativeButton("취소", null)
            .create()
    }
}