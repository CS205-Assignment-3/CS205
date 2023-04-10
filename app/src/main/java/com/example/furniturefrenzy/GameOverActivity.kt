package com.example.furniturefrenzy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class GameOverActivity : AppCompatActivity() {

    private lateinit var dbHelper: GameDatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        dbHelper = GameDatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        // Get the score extra from the intent
        val score = intent.getIntExtra("score", 0)
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score"

        // Get the score extra from the intent
        val timeTaken = intent.getIntExtra("timeTaken", 0)
        val timeTakenTextView: TextView = findViewById(R.id.timeTakenTextView)
        timeTakenTextView.text = "Time Taken: $timeTaken"

        // Save the game record to the database
        val datetime = System.currentTimeMillis()
        dbHelper.addGameRecord(datetime, timeTaken, score)

        val homeButton = findViewById<Button>(R.id.home)
        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(intent, 0)
        }

        val leaderboardButton = findViewById<Button>(R.id.leaderboard)
        leaderboardButton.setOnClickListener{
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(intent, 0)
        }

        // Show a dialog to ask for the user's name
//        showUsernameDialog { username ->
//            saveRecordToFirestore(username, datetime, timeTaken, score)
//        }
        saveRecordToFirestore("Jacky", datetime, timeTaken, score)
    }

//    private fun showUsernameDialog(onSubmit: (String) -> Unit) {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Enter your username")
//
//        val input = EditText(this)
//        builder.setView(input)
//
//        builder.setPositiveButton("Submit") { dialog, _ ->
//            val username = input.text.toString()
//            onSubmit(username)
//            dialog.dismiss()
//        }
//
//        builder.setNegativeButton("Cancel") { dialog, _ ->
//            dialog.cancel()
//        }
//
//        builder.show()
//    }

    private fun saveRecordToFirestore(username: String, datetime: Long, timeTaken: Int, score: Int) {
        val record = hashMapOf(
            "userName" to username,
            "dateTime" to datetime,
            "timeTaken" to timeTaken,
            "score" to score
        )

        firestore.collection("records")
            .add(record)
            .addOnSuccessListener { documentReference ->
                // Successfully added the record
                Log.i("firestore", "Upload successful")
            }
            .addOnFailureListener { e ->
                // Failed to add the record
                Log.e("firestore", "Upload failed")
            }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}