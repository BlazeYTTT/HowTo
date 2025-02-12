package com.blazik.howto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Инициализируем Toolbar для SettingsActivity
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)

        // Включаем кнопку "Назад" в Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Обработка нажатия на элементы меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {  // Обработка кнопки "Назад"
                onBackPressed() // Закрыть текущую активность
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
