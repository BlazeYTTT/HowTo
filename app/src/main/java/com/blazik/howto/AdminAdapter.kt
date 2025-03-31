package com.blazik.howto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class AdminAdapter(private val tutorials: List<PendingTutorial>) :
    RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMainCategory: TextView = view.findViewById(R.id.tv_main_category)
        val tvSubCategory: TextView = view.findViewById(R.id.tv_sub_category)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_tutorial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutorial = tutorials[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PendingTutorialDetailActivity::class.java).apply {
                putExtra("tutorial", tutorial) // Важно: tutorial не должен быть null
            }
            it.context.startActivity(intent)
        }
        holder.tvMainCategory.text = "Категория: ${tutorial.mainCategory}"
        holder.tvSubCategory.text = "Подкатегория: ${tutorial.subCategory}"
        holder.tvStatus.text = "Статус: ${tutorial.status}"

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PendingTutorialDetailActivity::class.java)
            intent.putExtra("tutorial", tutorial)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = tutorials.size
}