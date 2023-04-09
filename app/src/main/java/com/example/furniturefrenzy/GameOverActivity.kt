package com.example.furniturefrenzy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        // Get the score extra from the intent
        val score = intent.getIntExtra("SCORE", 0)
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score"

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
    }
}