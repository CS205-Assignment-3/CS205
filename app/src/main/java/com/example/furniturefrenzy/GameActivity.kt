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
    private lateinit var woodTextView: TextView
    private lateinit var stoneTextView: TextView
    private lateinit var glassTextView: TextView
    private lateinit var oreTextView: TextView
    private lateinit var plasticTextView: TextView
    private lateinit var craftCoffeeTableButton: Button
    private lateinit var craftFoldingChairButton: Button
    private lateinit var craftGlassTableButton: Button
    private lateinit var craftParkBenchButton: Button
    private lateinit var craftPlasticChairButton: Button
    private lateinit var craftStoneBenchButton: Button
    private lateinit var craftStoneTable: Button
    private lateinit var woodStationButton: Button
    private lateinit var stoneStationButton: Button
    private lateinit var glassStationButton: Button
    private lateinit var oreStationButton: Button
    private lateinit var plasticStationButton: Button
    private lateinit var gameOverButton: Button
    private val workerCount = 5 // Set number of worker here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Create game instance
        scoreTextView = findViewById(R.id.scoreTextView)
        workersTextView = findViewById(R.id.workersTextView)
        workersTextView.text = "$workerCount/$workerCount"
        woodTextView = findViewById(R.id.woodTextView)
        stoneTextView = findViewById(R.id.stoneTextView)
        glassTextView = findViewById(R.id.glassTextView)
        oreTextView = findViewById(R.id.oreTextView)
        plasticTextView = findViewById(R.id.plasticTextView)
        game = Game(this, this, workerCount, workersTextView, woodTextView,
            stoneTextView, glassTextView, oreTextView, plasticTextView, scoreTextView,)
        game.startGame()
        // Action listeners
//        craftCoffeeTableButton = findViewById(R.id.coffeeTable)
//        craftFoldingChairButton = findViewById(R.id.foldingChair)
//        craftGlassTableButton = findViewById(R.id.glassTable)
//        craftParkBenchButton = findViewById(R.id.parkBench)
//        craftPlasticChairButton = findViewById(R.id.plasticChair)
//        craftStoneBenchButton = findViewById(R.id.stoneBench)
//        craftStoneTable = findViewById(R.id.stoneTable)
        gameOverButton = findViewById(R.id.gameOver)

        // 5x resource sites
        // Wood -> 1; Stone -> 2; Glass -> 3; Ore -> 4; Plastic -> 5
        woodStationButton = findViewById(R.id.forest)
        woodStationButton.setOnClickListener {
            game.extractResource(1)
        }

        stoneStationButton = findViewById(R.id.digsite)
        stoneStationButton.setOnClickListener {
            game.extractResource(2)
        }

        glassStationButton = findViewById(R.id.smelters)
        glassStationButton.setOnClickListener {
            game.extractResource(3)
        }

        oreStationButton = findViewById(R.id.mine)
        oreStationButton.setOnClickListener {
            game.extractResource(4)
        }

        plasticStationButton = findViewById(R.id.factory)
        plasticStationButton.setOnClickListener {
            game.extractResource(5)
        }

        // 6x crafting bench (Inside dialogbox)
        // Produce crafting buttons
        // coffee table -> 1; folding chair -> 2; glass table -> 3; park bench -> 4; plastic chair -> 5; stone bench -> 6; stone table -> 7
        /*
        craftCoffeeTableButton.setOnClickListener {
            game.craftOrder(1)
        }

        craftFoldingChairButton.setOnClickListener {
            game.craftOrder(2)
        }

        craftGlassTableButton.setOnClickListener {
            game.craftOrder(3)
        }

        craftParkBenchButton.setOnClickListener {
            game.craftOrder(4)
        }

        craftPlasticChairButton.setOnClickListener {
            game.craftOrder(5)
        }

        craftStoneBenchButton.setOnClickListener {
            game.craftOrder(6)
        }

        craftStoneTable.setOnClickListener {
            game.craftOrder(7)
        }
         */
        gameOverButton.setOnClickListener {
            game.showGameOverScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        game = Game(this, this, workerCount, workersTextView, woodTextView,
            stoneTextView, glassTextView, oreTextView, plasticTextView, scoreTextView,) // Create a new game instance when returning to the activity
    }
}
