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
    private lateinit var craftOrderButton: Button
    private lateinit var gameOverButton: Button
    private val workerCount = 5 // Set number of worker here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Create game instance
        scoreTextView = findViewById(R.id.scoreTextView)
        workersTextView = findViewById(R.id.workersTextView)
        workersTextView.text = "$workerCount/$workerCount"
        game = Game(this, this, workerCount, scoreTextView, workersTextView)


        // Action listeners
        craftOrderButton = findViewById(R.id.craftOrder)
        gameOverButton = findViewById(R.id.gameOver)

        // 5x resource sites
        // rockStationButton... {game.extractResource(1)}


        // 6x crafting bench (Show dialogbox)


        // Produce crafting buttons
        // Plastic chair craft button
        craftOrderButton.setOnClickListener {
            game.craftOrder(1)
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
