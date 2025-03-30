package com.blazik.howto

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
        val btnApprove: Button = view.findViewById(R.id.btn_approve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_tutorial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutorial = tutorials[position]
        holder.tvMainCategory.text = tutorial.mainCategory
        holder.tvSubCategory.text = tutorial.subCategory

        holder.btnApprove.setOnClickListener {
            approveTutorial(tutorial.key)
        }
    }

    private fun approveTutorial(key: String?) {
        if (key == null) return
        val database = FirebaseDatabase.getInstance().getReference("PendingTutorials")
        database.child(key).child("status").setValue("approved")
    }

    override fun getItemCount() = tutorials.size
}