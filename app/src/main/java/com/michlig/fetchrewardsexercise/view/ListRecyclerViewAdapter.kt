package com.michlig.fetchrewardsexercise.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchrewardsexercise.R
import com.michlig.fetchrewardsexercise.model.ListEntry

class ListRecyclerViewAdapter(private val entries: List<List<ListEntry>>): RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>() {
    private var listVisible = false

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val idRecycler: RecyclerView = itemView.findViewById(R.id.id_recycler)
        val minimizeButton: ImageButton = itemView.findViewById(R.id.minimize_icon)
        val listNumber: TextView = itemView.findViewById(R.id.list_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val idList = entries[position].sorted()
        val idNumber = position+1
        holder.idRecycler.apply {
            adapter = IdRecyclerViewAdapter(idList)
            layoutManager = LinearLayoutManager(holder.itemView.context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        holder.listNumber.text = idNumber.toString()

        holder.minimizeButton.setOnClickListener{
            if(!listVisible){
                holder.minimizeButton.setImageResource(R.drawable.remove_48px)
                holder.idRecycler.visibility = View.VISIBLE
            }else{
                holder.minimizeButton.setImageResource(R.drawable.add_48px)
                holder.idRecycler.visibility = View.GONE
            }
            listVisible = !listVisible
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}