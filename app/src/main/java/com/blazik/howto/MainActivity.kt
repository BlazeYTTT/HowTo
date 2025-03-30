package com.blazik.howto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import android.widget.Button
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var buttonContainer: LinearLayout
    private lateinit var database: DatabaseReference
    private val buttonsList = mutableListOf<Pair<Button, LinearLayout>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация элементов
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        buttonContainer = findViewById(R.id.button_container)

        // Настройка ActionBarDrawerToggle для открывания и закрывания Drawer
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Настройка кастомных пунктов меню
        setupCustomMenu()

        // Инициализация базы данных Firebase
        database = FirebaseDatabase.getInstance().getReference("Buttons")
        database.orderByChild("status").equalTo("approved")
        loadButtonsFromFirebase()
    }

    private fun setupCustomMenu() {
        // Очистите стандартное меню
        navigationView.menu.clear()

        // Получение шапки NavigationView
        val menu = navigationView.menu


        val items = listOf(
            Pair("Выход", android.R.drawable.ic_menu_close_clear_cancel)
        )

        for (item in items) {
            val menuItem = menu.add(item.first)
            menuItem.icon = ContextCompat.getDrawable(this, item.second)
            menuItem.setOnMenuItemClickListener {
                when (item.first) {
                    "Выход" -> finish()
                }
                drawerLayout.closeDrawers()
                true
            }
        }
    }




    // Разметка меню в Toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterButtons(newText.orEmpty())
                return true
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create -> {
                startActivity(Intent(this, CreateTutorialActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Фильтрация кнопок по запросу
    private fun filterButtons(query: String) {
        if (query == "admin123") {
            startActivity(Intent(this, AdminActivity::class.java))
            return
        }
        buttonContainer.removeAllViews()
        for (pair in buttonsList) {
            val button = pair.first
            if (button.text.toString().contains(query, ignoreCase = true)) {
                buttonContainer.addView(pair.second)
            }
        }
    }

    // Загрузка кнопок из Firebase
    private fun loadButtonsFromFirebase() {
        database.orderByChild("pos").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                buttonContainer.removeAllViews()
                buttonsList.clear()
                for (buttonSnapshot in snapshot.children) {
                    val pos = buttonSnapshot.child("pos").value.toString()
                    val text = buttonSnapshot.child("text").value.toString()
                    val imageUrl = buttonSnapshot.child("imageUrl").value?.toString() ?: ""

                    val buttonLayout = createButtonWithImage(text, imageUrl, pos)
                    buttonsList.add(buttonLayout)
                    buttonContainer.addView(buttonLayout.second)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Создание кнопки с изображением
    private fun createButtonWithImage(
        text: String,
        imageUrl: String,
        pos: String
    ): Pair<Button, LinearLayout> {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 8, 8, 8) }
        }

        // Создаем ShapeableImageView с закругленными верхними углами
        val imageView = ShapeableImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            )
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopLeftCornerSize(50f)
                .setTopRightCornerSize(50f)
                .setBottomLeftCornerSize(0f)
                .setBottomRightCornerSize(0f)
                .build()
        }

        // Загружаем изображение с помощью Glide
        Glide.with(this@MainActivity)
            .load(imageUrl)
            .into(imageView)

        // Обработчик нажатия на изображение
        imageView.setOnClickListener {
            val intent = Intent(this@MainActivity, SubButtonActivity::class.java)
            intent.putExtra("buttonPos", pos)
            startActivity(intent)
        }

        // Создаем кнопку
        val button = Button(this).apply {
            val color = ContextCompat.getColor(context, R.color.buttonBackground)
            setBackgroundColor(color)
            this.text = text  // Используем переданное значение

            // Создаем GradientDrawable для закругленных нижних углов
            val drawable = GradientDrawable().apply {
                setColor(color)
                cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 50f, 50f, 50f, 50f)
            }
            background = drawable

            // Обработчик нажатия на кнопку
            setOnClickListener {
                val intent = Intent(this@MainActivity, SubButtonActivity::class.java)
                intent.putExtra("buttonPos", pos)
                startActivity(intent)
            }
        }


        layout.addView(imageView)
        layout.addView(button)
        return button to layout
    }
}


