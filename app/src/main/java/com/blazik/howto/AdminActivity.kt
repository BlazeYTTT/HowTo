package com.blazik.howto

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.LinearLayoutManager

class AdminActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tutorial)

        // Исправленная строка 32 (была опечатка в setOnClickListener)
        findViewById<Button>(R.id.btn_submit).setOnClickListener {
        }
    }

    private fun loadPendingTutorials() {
        database.orderByChild("status").equalTo("pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tutorials = mutableListOf<PendingTutorial>()
                    for (tutorialSnapshot in snapshot.children) {
                        val tutorial = tutorialSnapshot.getValue(PendingTutorial::class.java)
                        tutorial?.key = tutorialSnapshot.key
                        tutorial?.let { tutorials.add(it) }
                    }
                    recyclerView.adapter = AdminAdapter(tutorials)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

}

data class PendingTutorial(
    var key: String? = null,
    val mainCategory: String = "",
    val subCategory: String = "",
    val videoUrl: String = "",
    val description: String = "",
    val status: String = ""
)