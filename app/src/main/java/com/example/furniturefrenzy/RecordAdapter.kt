package com.example.furniturefrenzy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(private val records: List<Record>) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val serialTextView: TextView = view.findViewById(R.id.serialTextView)
        val dateTimeTextView: TextView = view.findViewById(R.id.dateTimeTextView)
        val timeTakenTextView: TextView = view.findViewById(R.id.timeTakenTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        holder.serialTextView.text = "$position."
        holder.dateTimeTextView.text = dateFormat.format(Date(record.dateTime.toLong()))
        holder.timeTakenTextView.text = record.timeTaken.toString()
        holder.scoreTextView.text = record.score.toString()
    }

    override fun getItemCount(): Int {
        return records.size
    }
}
