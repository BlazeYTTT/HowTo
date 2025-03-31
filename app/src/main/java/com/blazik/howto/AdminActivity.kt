package com.blazik.howto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin) // Используем правильный layout

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("PendingTutorials")
        loadPendingTutorials()
    }

    private fun loadPendingTutorials() {
        database.orderByChild("status").equalTo("pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@AdminActivity,
                            "Нет заявок на модерацию",
                            Toast.LENGTH_SHORT).show()
                        return
                    }

                    val tutorials = mutableListOf<PendingTutorial>()
                    for (tutorialSnapshot in snapshot.children) {
                        val tutorial = tutorialSnapshot.getValue(PendingTutorial::class.java)
                        tutorial?.key = tutorialSnapshot.key
                        tutorial?.let {
                            Log.d("ADMIN_DEBUG", "Found tutorial: ${it.mainCategory}")
                            tutorials.add(it)
                        }
                    }

                    if (tutorials.isEmpty()) {
                        Toast.makeText(this@AdminActivity,
                            "Список заявок пуст",
                            Toast.LENGTH_SHORT).show()
                    }

                    recyclerView.adapter = AdminAdapter(tutorials)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminActivity,
                        "Ошибка загрузки: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }
}