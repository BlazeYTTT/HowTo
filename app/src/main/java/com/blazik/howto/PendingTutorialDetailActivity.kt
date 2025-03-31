package com.blazik.howto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingTutorialDetailActivity : AppCompatActivity() {
    private lateinit var tutorial: PendingTutorial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_tutorial_detail)
        tutorial = intent.getSerializableExtra("tutorial") as PendingTutorial
        setContentView(R.layout.activity_pending_tutorial_detail)

        tutorial = intent.getParcelableExtra<PendingTutorial>("tutorial") ?: run {
            Toast.makeText(this, "Ошибка: данные не загружены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // Отображение данных
        findViewById<TextView>(R.id.tv_main_category).text = tutorial.mainCategory
        findViewById<TextView>(R.id.tv_sub_category).text = tutorial.subCategory
        findViewById<TextView>(R.id.tv_video_url).text = tutorial.videoUrl
        findViewById<TextView>(R.id.tv_description).text = tutorial.description

        // Обработка кнопок
        findViewById<Button>(R.id.btn_approve).setOnClickListener { approveTutorial() }
        findViewById<Button>(R.id.btn_reject).setOnClickListener { rejectTutorial() }
    }

    private fun approveTutorial() {
        val database = FirebaseDatabase.getInstance().reference

        // Добавление в Buttons
        database.child("Buttons").child(tutorial.mainPos.toString()).setValue(
            hashMapOf(
                "text" to tutorial.mainCategory,
                "imageUrl" to tutorial.imageUrl,
                "pos" to tutorial.mainPos
            )
        )

        // Добавление в SubButtons
        database.child("SubButtons").child(tutorial.subPos).setValue(
            hashMapOf(
                "text" to tutorial.subCategory,
                "imageUrl" to tutorial.imageUrl,
                "pos" to tutorial.subPos
            )
        )

        // Добавление в Videos
        database.child("Videos").child(tutorial.subPos).setValue(
            hashMapOf(
                "text" to tutorial.description,
                "url" to tutorial.videoUrl,
                "screen" to tutorial.subPos
            )
        )

        // Обновление статуса
        database.child("PendingTutorials").child(tutorial.key!!).child("status").setValue("approved")
        finish()
    }

    private fun rejectTutorial() {
        FirebaseDatabase.getInstance().reference
            .child("PendingTutorials")
            .child(tutorial.key!!)
            .child("status")
            .setValue("rejected")
        finish()
    }
}