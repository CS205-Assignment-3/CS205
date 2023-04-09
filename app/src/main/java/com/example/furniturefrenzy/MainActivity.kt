package com.example.furniturefrenzy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectLevelButton = findViewById<Button>(R.id.select_level)
        selectLevelButton.setOnClickListener{
            val intent = Intent(this, SelectLevelActivity::class.java)
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