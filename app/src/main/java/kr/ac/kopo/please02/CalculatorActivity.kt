package kr.ac.kopo.please02

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kr.ac.kopo.please02.databinding.ActivityCalculatorBinding
import java.util.Locale
import java.util.Stack

class CalculatorActivity : AppCompatActivity() {
    private lateinit var viewbinding: ActivityCalculatorBinding
    private lateinit var classifier: Classifier
    private var expression = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        classifier = Classifier(this)

        val Bule = ContextCompat.getColor(this, R.color.Blue)
        val blackColor = ContextCompat.getColor(this, R.color.white)


        viewbinding.drawView.setStrokeWidth(40.0f)
        viewbinding.drawView.setBackgroundColor(Bule)
        viewbinding.drawView.setColor(blackColor)

        viewbinding.btnAdd.setOnClickListener {
            val bitmap = viewbinding.drawView.getBitmap()
            val res = classifier.classify(bitmap)
            val outnumber = String.format(Locale.ENGLISH, "%d", res.first)
            val outstr = String.format(Locale.ENGLISH, "%d, %.0f%%", res.first, res.second*100.0f)
            expression.append(outnumber)
            viewbinding.expression.text = expression.toString()
            viewbinding.textView1.text = outstr
            viewbinding.drawView.clearCanvas()
        }

        viewbinding.btnPlus.setOnClickListener {
            expression.append(" + ")
            viewbinding.expression.text = expression.toString()
        }

        viewbinding.btnMinus.setOnClickListener {
            expression.append(" - ")
            viewbinding.expression.text = expression.toString()
        }

        viewbinding.btnDivide.setOnClickListener {
            expression.append(" / ")
            viewbinding.expression.text = expression.toString()
        }

        viewbinding.btnMultiply.setOnClickListener {
            expression.append(" * ")
            viewbinding.expression.text = expression.toString()
        }

        viewbinding.btnEquals.setOnClickListener {
            val result = evaluateExpression(expression.toString())
            viewbinding.textView3.text = result.toString()
        }

        viewbinding.btnDelete.setOnClickListener {
            if (expression.isNotEmpty()) {
                expression.setLength(expression.length - 1)
                viewbinding.expression.text = expression.toString()
            }
        }

        viewbinding.btnAllDelete.setOnClickListener {
            expression.clear()
            viewbinding.expression.text = ""
            viewbinding.textView3.text = ""
        }
        viewbinding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return try {
            val tokens = expression.split(" ")
            val values = Stack<Double>()
            val ops = Stack<String>()

            for (token in tokens) {
                when {
                    token.toDoubleOrNull() != null -> values.push(token.toDouble())
                    token == "(" -> ops.push(token)
                    token == ")" -> {
                        while (ops.peek() != "(")
                            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                        ops.pop()
                    }
                    token == "+" || token == "-" || token == "*" || token == "/" -> {
                        while (!ops.empty() && hasPrecedence(token, ops.peek()))
                            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                        ops.push(token)
                    }
                }
            }

            while (!ops.empty())
                values.push(applyOp(ops.pop(), values.pop(), values.pop()))

            values.pop()
        } catch (e: Exception) {
            0.0
        }
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op2 == "(" || op2 == ")")
            return false
        if ((op1 == "*" || op1 == "/") && (op2 == "+" || op2 == "-"))
            return false
        return true
    }

    private fun applyOp(op: String, b: Double, a: Double): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else Double.NaN
            else -> 0.0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.finish()
    }
}
