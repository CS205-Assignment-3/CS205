package com.example.furniturefrenzy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: GameDatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        dbHelper = GameDatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        val records = dbHelper.getAllRecords()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecordAdapter(records, false)

        val globalRecords = mutableListOf<Record>()
        val globalRecyclerView: RecyclerView = findViewById(R.id.globalRecyclerView)
        globalRecyclerView.layoutManager = LinearLayoutManager(this)
        firestore.collection("records")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val record = Record(
                        uuid = document.id,
                        dateTime = document["dateTime"].toString(),
                        timeTaken = document["timeTaken"].toString().toInt(),
                        score = document["score"].toString().toInt(),
                        userName = document["userName"].toString()
                    )
                    globalRecords.add(record)
                }
                globalRecyclerView.adapter = RecordAdapter(globalRecords, true)
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents: ", exception)
            }

        val homeButton = findViewById<Button>(R.id.home)
        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(intent, 0)
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

}