package com.blazik.howto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateTutorialActivity : AppCompatActivity() {
    private lateinit var etMainCategory: EditText
    private lateinit var etSubCategory: EditText
    private lateinit var etVideoUrl: EditText
    private lateinit var etDescription: EditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tutorial)

        database = FirebaseDatabase.getInstance().getReference("PendingTutorials")

        etMainCategory = findViewById(R.id.et_main_category)
        etSubCategory = findViewById(R.id.et_sub_category)
        etVideoUrl = findViewById(R.id.et_video_url)
        etDescription = findViewById(R.id.et_description)

        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            submitTutorial()
        }
    }

    private fun submitTutorial() {
        val tutorial = hashMapOf(
            "mainCategory" to etMainCategory.text.toString(),
            "subCategory" to etSubCategory.text.toString(),
            "videoUrl" to etVideoUrl.text.toString(),
            "description" to etDescription.text.toString(),
            "status" to "pending"
        )

        database.push().setValue(tutorial).addOnCompleteListener {
            Toast.makeText(this, "Tutorial submitted for review", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}