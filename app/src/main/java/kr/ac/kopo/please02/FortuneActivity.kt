package kr.ac.kopo.please02

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class FortuneActivity : AppCompatActivity() {

    private lateinit var fortuneText: TextView
    private lateinit var fortuneImage: ImageView
    private lateinit var btnHome:Button
    private val fortunes = arrayOf(
        "힘든 날이 되겠군요" to R.drawable.bad1,
        "주의가 필요한 하루입니다." to R.drawable.bad2,
        "조금 지치는 날이 되겠군요. 그치만 포기하지마세요" to R.drawable.bad3,
        "조금 자중할 필요가 있습니다." to R.drawable.bad4,
        "우와 오늘은 어떤 일이던 해낼 것같은데요?" to R.drawable.can,
        "행운이 가득한 하루가 되겠어요!" to R.drawable.good,
        "행운을 찾는 하루가 될거예요" to R.drawable.lucky,
        "혹시 바라던 일이 있을실까요? 오늘 이루어질거예요!" to R.drawable.wish
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fortune)

        fortuneText = findViewById(R.id.text_fortune)
        fortuneImage = findViewById(R.id.image_fortune)
        btnHome = findViewById(R.id.btnHome)
        val buttonGetFortune: Button = findViewById(R.id.button_get_fortune)

        buttonGetFortune.setOnClickListener {
            val randomIndex = Random.nextInt(fortunes.size)
            val (fortune, imageRes) = fortunes[randomIndex]
            fortuneText.text = fortune
            fortuneImage.setImageResource(imageRes)
        }
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}