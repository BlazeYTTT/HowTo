package com.blazik.howto

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

@Suppress("DEPRECATION")
class VideoActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var videoScreen: String
    private lateinit var webView: WebView
    private lateinit var description: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var fab: FloatingActionButton
    private var isExtendedReadingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Устанавливаем тулбар
        val toolbar = findViewById<Toolbar>(R.id.video_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary))
        toolbar.setTitleTextColor(getColor(R.color.backgroundColor))

        // Настройка отображения видео и описания
        videoScreen = intent.getStringExtra("videoScreen") ?: "1_1"
        val videoFrame = findViewById<FrameLayout>(R.id.video_frame)
        description = findViewById(R.id.description_text)
        scrollView = findViewById(R.id.scroll_view)

        database = FirebaseDatabase.getInstance().getReference("Videos").child(videoScreen)

        // Добавляем WebView программно
        webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                calculateAspectRatioHeight(16, 9)
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
        }
        videoFrame.addView(webView)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val url = snapshot.child("url").value.toString()
                val text = snapshot.child("text").value.toString()
                val formattedText: String = text.replace("\\n", "\n")

                webView.loadUrl(url)
                description.text = formattedText
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })

        // Floating Action Button для режима расширенного чтения
        fab = findViewById(R.id.fab_mode_toggle)
        fab.setOnClickListener {
            toggleExtendedReadingMode()
        }
    }

    private fun toggleExtendedReadingMode() {
        if (isExtendedReadingMode) {
            // Возврат к стандартному режиму
            webView.visibility = View.VISIBLE
            scrollView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // Используем вес, чтобы ScrollView занимал оставшееся пространство
            )
            fab.setImageResource(R.drawable.ic_search) // Иконка для перехода в расширенный режим
        } else {
            // Включение режима расширенного чтения
            webView.visibility = View.GONE
            scrollView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            fab.setImageResource(R.drawable.ic_search) // Иконка для выхода из расширенного режима
        }
        fab.visibility = View.VISIBLE // Убедиться, что кнопка всегда видима
        isExtendedReadingMode = !isExtendedReadingMode
    }

    // Функция для вычисления высоты с заданным соотношением сторон
    private fun calculateAspectRatioHeight(widthRatio: Int, heightRatio: Int): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        return screenWidth * heightRatio / widthRatio
    }

    private fun TextView.replace(s: String, s1: String) {}
}
