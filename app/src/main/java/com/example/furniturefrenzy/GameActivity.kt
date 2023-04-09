package com.example.furniturefrenzy

import Game
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    private lateinit var game: Game
    private lateinit var scoreTextView: TextView
    private lateinit var workersTextView: TextView
    private lateinit var incrementScoreButton: Button
    private lateinit var gameOverButton: Button
    private val workerCount = 2 // Set number of worker here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Create game instance
        scoreTextView = findViewById(R.id.scoreTextView)
        workersTextView = findViewById(R.id.workersTextView)
        game = Game(this, this, workerCount, scoreTextView, workersTextView)


        // Action listeners
        incrementScoreButton = findViewById(R.id.craftOrder)
        gameOverButton = findViewById(R.id.gameOver)
        incrementScoreButton.setOnClickListener {
            game.craftOrder()
        }

        gameOverButton.setOnClickListener {
            game.showGameOverScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        game = Game(this, this, workerCount, scoreTextView, workersTextView) // Create a new game instance when returning to the activity
    }
}
