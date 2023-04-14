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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: GameDatabaseHelper
    private lateinit var firestore: FirebaseFirestore
    private lateinit var globalRecyclerView: RecyclerView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        dbHelper = GameDatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        val records = dbHelper.getAllRecords()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecordAdapter(records, false)

        globalRecyclerView = findViewById(R.id.globalRecyclerView)
        globalRecyclerView.layoutManager = LinearLayoutManager(this)

        val homeButton = findViewById<Button>(R.id.home)
        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(intent, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchGlobalRecords()
    }

    private fun fetchGlobalRecords() {
        coroutineScope.launch {
            try {
                val globalRecords = mutableListOf<Record>()
                val documents = firestore.collection("records")
                    .orderBy("score", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .await()

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
            } catch (exception: Exception) {
                Log.w("Firebase", "Error getting documents: ", exception)
            }
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        coroutineScope.cancel() // Cancel coroutines when the activity is destroyed
        super.onDestroy()
    }

}


