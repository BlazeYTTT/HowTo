package com.blazik.howto

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.*

class SubButtonActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var buttonContainer: LinearLayout
    private lateinit var buttonPos: String
    private val buttonsList = mutableListOf<Pair<Button, LinearLayout>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_button)

        val toolbar = findViewById<Toolbar>(R.id.sub_toolbar)
        setSupportActionBar(toolbar)

        buttonContainer = findViewById(R.id.sub_button_container)
        buttonPos = intent.getStringExtra("buttonPos") ?: "1"
        database = FirebaseDatabase.getInstance().getReference("SubButtons")

        loadButtonsFromFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Buttons"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterButtons(newText.orEmpty())
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterButtons(query: String) {
        buttonContainer.removeAllViews()
        for (pair in buttonsList) {
            val button = pair.first
            if (button.text.toString().contains(query, ignoreCase = true)) {
                buttonContainer.addView(pair.second)
            }
        }
    }

    private fun loadButtonsFromFirebase() {
        database.orderByChild("pos").startAt(buttonPos).endAt("${buttonPos}_z")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    buttonContainer.removeAllViews()
                    buttonsList.clear()
                    for (subButtonSnapshot in snapshot.children) {
                        val pos = subButtonSnapshot.child("pos").value.toString()
                        val text = subButtonSnapshot.child("text").value.toString()
                        val imageUrl = subButtonSnapshot.child("imageUrl").value?.toString() ?: ""

                        val buttonLayout = createButtonWithImage(text, imageUrl, pos)
                        buttonsList.add(buttonLayout)
                        buttonContainer.addView(buttonLayout.second)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun createButtonWithImage(text: String, imageUrl: String, pos: String): Pair<Button, LinearLayout> {
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
                .build()
        }

        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(imageView)

        // Делаем изображение кликабельным
        imageView.setOnClickListener {
            val intent = Intent(this@SubButtonActivity, VideoActivity::class.java)
            intent.putExtra("videoScreen", pos)
            startActivity(intent)
        }

        // Создаем кнопку с закругленными нижними углами
        val button = Button(this).apply {
            val color = ContextCompat.getColor(context, R.color.buttonBackground)
            this.setBackgroundColor(color)
            this.text = text

            // Создаем GradientDrawable с закругленными нижними углами
            val drawable = GradientDrawable().apply {
                setColor(color)
                cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 50f, 50f, 50f, 50f)
            }
            background = drawable

            setOnClickListener {
                val intent = Intent(this@SubButtonActivity, VideoActivity::class.java)
                intent.putExtra("videoScreen", pos)
                startActivity(intent)
            }
        }

        layout.addView(imageView)
        layout.addView(button)
        return button to layout
    }
}
