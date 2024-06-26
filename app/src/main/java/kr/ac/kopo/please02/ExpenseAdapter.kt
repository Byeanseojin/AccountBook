package kr.ac.kopo.please02

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ExpenseAdapter(context: Context, private val expenses: List<ExpenseActivity.Expense>) :
    ArrayAdapter<ExpenseActivity.Expense>(context, 0, expenses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.expense_list_item, parent, false
        )
        val expense = getItem(position)
        view.findViewById<TextView>(R.id.tvTitle).text = expense?.title
        view.findViewById<TextView>(R.id.tvCategory).text = expense?.category
        view.findViewById<TextView>(R.id.tvAmount).text = expense?.amount.toString()
        view.findViewById<TextView>(R.id.tvDate).text = expense?.date
        return view
    }
}
