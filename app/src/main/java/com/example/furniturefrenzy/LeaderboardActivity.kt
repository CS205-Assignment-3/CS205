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

    // Declare Dbhelper and CoroutineScope
    private lateinit var dbHelper: GameDatabaseHelper
    private lateinit var firestore: FirebaseFirestore
    private lateinit var globalRecyclerView: RecyclerView
    private lateinit var localRecyclerView: RecyclerView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Init helper class
        dbHelper = GameDatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        // Init recycler view
        localRecyclerView = findViewById(R.id.recyclerView)
        localRecyclerView.layoutManager = LinearLayoutManager(this)

        globalRecyclerView = findViewById(R.id.globalRecyclerView)
        globalRecyclerView.layoutManager = LinearLayoutManager(this)

        // Action listeners
        val homeButton = findViewById<Button>(R.id.home)
        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(intent, 0)
        }

        val clearAllButton = findViewById<Button>(R.id.clear)
        clearAllButton.setOnClickListener {
            dbHelper.deleteAllRecords()
            // Refresh the local leaderboard after deletion
            val records = dbHelper.getAllRecords()
            loadLocalRecords()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update the recycler view with the latest record
        loadLocalRecords()
        fetchGlobalRecords()
    }

    private fun fetchGlobalRecords() {
        // Async fetch for the firebase records
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

    private fun loadLocalRecords() {
        val records = dbHelper.getAllRecords()
        localRecyclerView.adapter = RecordAdapter(records, false)
    }

    override fun onDestroy() {
        dbHelper.close()
        coroutineScope.cancel() // Cancel coroutines when the activity is destroyed
        super.onDestroy()
    }

}


