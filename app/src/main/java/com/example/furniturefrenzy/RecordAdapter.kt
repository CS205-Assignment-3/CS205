package com.example.furniturefrenzy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(private val records: List<Record>, private val isGlobal: Boolean) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_NORMAL = 1
    }

    class RecordViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val serialTextView: TextView = view.findViewById(R.id.serialTextView)
        val dateTimeTextView: TextView = view.findViewById(R.id.dateTimeTextView)
        val timeTakenTextView: TextView = view.findViewById(R.id.timeTakenTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val layout = if (viewType == ITEM_TYPE_HEADER) R.layout.item_header else R.layout.item_record
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        if (position > 0) {
            val record = records[position - 1]
            val minutes = record.timeTaken / 60000
            val seconds = (record.timeTaken % 60000) / 1000
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            holder.serialTextView.text = "$position."
            holder.timeTakenTextView.text = "Time Taken: $minutes min ${seconds.toInt()} sec"
            holder.scoreTextView.text = record.score.toString()
            if (isGlobal) {
                holder.userNameTextView.text = record.userName
            } else {
                holder.dateTimeTextView.text = dateFormat.format(Date(record.dateTime.toLong()))
            }
        }
        if (isGlobal) {
            holder.userNameTextView.visibility = View.VISIBLE
            holder.dateTimeTextView.visibility = View.GONE
        } else {
            holder.userNameTextView.visibility = View.GONE
            holder.dateTimeTextView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return records.size + 1
    }
}
