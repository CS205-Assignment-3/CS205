package com.example.furniturefrenzy

import Game
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable


class GameActivity : AppCompatActivity() {
    private lateinit var game: Game
    private var context: Context = this
    private lateinit var scoreTextView: TextView
    private lateinit var craft_1: Button
    private lateinit var craft_2: Button
    private lateinit var craft_3: Button
    private lateinit var craft_4: Button
    private lateinit var craft_5: Button
    private lateinit var craft_6: Button
    private lateinit var workersTextView: TextView
    private lateinit var woodTextView: TextView
    private lateinit var stoneTextView: TextView
    private lateinit var glassTextView: TextView
    private lateinit var oreTextView: TextView
    private lateinit var plasticTextView: TextView
    //private lateinit var craftCoffeeTableButton: Button
    //private lateinit var craftFoldingChairButton: Button
    //private lateinit var craftGlassTableButton: Button
    //private lateinit var craftParkBenchButton: Button
    //private lateinit var craftPlasticChairButton: Button
    //private lateinit var craftStoneBenchButton: Button
    //private lateinit var craftStoneTable: Button
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

        //show dialogBox
        // 6x crafting bench (Inside dialogbox)
        // Produce crafting buttons
        // coffee table -> 1; folding chair -> 2; glass table -> 3; park bench -> 4; plastic chair -> 5; stone bench -> 6; stone table -> 7
        fun showDialogBox() {
            val dialogBinding = layoutInflater.inflate(R.layout.dialogbox, null)
            val dialogBox = Dialog(this)
            dialogBox.setContentView(dialogBinding)
            dialogBox.setCancelable(true)
            dialogBox.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBox.show()
            var craftCoffeeTableButton = dialogBinding.findViewById<Button>(R.id.coffeeTable)
            var craftFoldingChairButton = dialogBinding.findViewById<Button>(R.id.foldingChair)
            var craftGlassTableButton = dialogBinding.findViewById<Button>(R.id.glassTable)
            var craftParkBenchButton = dialogBinding.findViewById<Button>(R.id.parkBench)
            var craftPlasticChairButton = dialogBinding.findViewById<Button>(R.id.plasticChair)
            var craftStoneBenchButton = dialogBinding.findViewById<Button>(R.id.stoneBench)
            var craftStoneTable = dialogBinding.findViewById<Button>(R.id.stoneTable)
            craftCoffeeTableButton.setOnClickListener {
                game.craftOrder(1)
                dialogBox.dismiss()
            }
            craftFoldingChairButton.setOnClickListener {
                game.craftOrder(2)
                dialogBox.dismiss()
            }
            craftGlassTableButton.setOnClickListener {
                game.craftOrder(3)
                dialogBox.dismiss()
            }
            craftParkBenchButton.setOnClickListener {
                game.craftOrder(4)
                dialogBox.dismiss()
            }
            craftPlasticChairButton.setOnClickListener {
                game.craftOrder(5)
                dialogBox.dismiss()
            }
            craftStoneBenchButton.setOnClickListener {
                game.craftOrder(6)
                dialogBox.dismiss()
            }
            craftStoneTable.setOnClickListener {
                game.craftOrder(7)
                dialogBox.dismiss()
            }
        }

        //Click on crafter -> Show dialogBox
        craft_1 = findViewById(R.id.craft_1)
        craft_2 = findViewById(R.id.craft_2)
        craft_3 = findViewById(R.id.craft_3)
        craft_4 = findViewById(R.id.craft_4)
        craft_5 = findViewById(R.id.craft_5)
        craft_6 = findViewById(R.id.craft_6)

        craft_1.setOnClickListener{
            showDialogBox()
        }
        craft_2.setOnClickListener{
            showDialogBox()
        }
        craft_3.setOnClickListener{
            showDialogBox()
        }
        craft_4.setOnClickListener{
            showDialogBox()
        }
        craft_5.setOnClickListener{
            showDialogBox()
        }
        craft_6.setOnClickListener{
            showDialogBox()
        }

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

    }

    override fun onResume() {
        super.onResume()
        game = Game(this, this, workerCount, workersTextView, woodTextView,
            stoneTextView, glassTextView, oreTextView, plasticTextView, scoreTextView,) // Create a new game instance when returning to the activity
    }
}
