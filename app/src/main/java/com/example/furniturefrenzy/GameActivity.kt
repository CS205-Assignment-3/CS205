package com.example.furniturefrenzy

import Game
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.View
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class GameActivity : AppCompatActivity() {
    private lateinit var game: Game
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
    private lateinit var orderImageView1: ImageView
    private lateinit var orderImageView2: ImageView
    private lateinit var orderImageView3: ImageView
    private lateinit var orderImageView4: ImageView
    private lateinit var orderImageView5: ImageView
    private lateinit var orderImageView6: ImageView
    private lateinit var orderImageView7: ImageView
    private lateinit var orderImageView8: ImageView
    private lateinit var orderImageView9: ImageView
    private lateinit var orderImageView10: ImageView
    private lateinit var woodStationButton: Button
    private lateinit var stoneStationButton: Button
    private lateinit var glassStationButton: Button
    private lateinit var oreStationButton: Button
    private lateinit var plasticStationButton: Button
    private val workerCount = 5 // Set number of worker here
    private lateinit var currentRequestList: ArrayList<finalResource>
    private var resources = ConcurrentHashMap<String, AtomicInteger>()
    private var score = AtomicInteger()

    data class finalResource(val name: String, val requiredResource: List<Int>)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //initialise currentRequestList
        currentRequestList = ArrayList<finalResource>()
        //initialise resources
        resources["Logs"] = AtomicInteger(0)
        resources["Stone"] = AtomicInteger(0)
        resources["Ore"] = AtomicInteger(0)
        resources["PlasticRods"] = AtomicInteger(0)
        resources["Glass"] = AtomicInteger(0)
        score = AtomicInteger(0)
        // Create game instance
        scoreTextView = findViewById(R.id.scoreTextView)
        workersTextView = findViewById(R.id.workersTextView)
        workersTextView.text = "$workerCount/$workerCount"
        woodTextView = findViewById(R.id.woodTextView)
        stoneTextView = findViewById(R.id.stoneTextView)
        glassTextView = findViewById(R.id.glassTextView)
        oreTextView = findViewById(R.id.oreTextView)
        plasticTextView = findViewById(R.id.plasticTextView)
        orderImageView1 = findViewById(R.id.order_1)
        orderImageView2 = findViewById(R.id.order_2)
        orderImageView3 = findViewById(R.id.order_3)
        orderImageView4 = findViewById(R.id.order_4)
        orderImageView5 = findViewById(R.id.order_5)
        orderImageView6 = findViewById(R.id.order_6)
        orderImageView7 = findViewById(R.id.order_7)
        orderImageView8 = findViewById(R.id.order_8)
        orderImageView9 = findViewById(R.id.order_9)
        orderImageView10 = findViewById(R.id.order_10)

        val orderArray = arrayOf(orderImageView1,orderImageView2,orderImageView3,orderImageView4,
                                orderImageView5,orderImageView6,orderImageView7,orderImageView8,
                                orderImageView9,orderImageView10)
        game = Game(this, this, workerCount, workersTextView, woodTextView,
            stoneTextView, glassTextView, oreTextView, plasticTextView, scoreTextView,
            orderArray, currentRequestList,resources, score)
        game.startGame()

        // Worker GIF display
        // Function to show a GifImageView for a specific period of time
        fun showGifImageView(gifImageView: pl.droidsonroids.gif.GifImageView, delayTime: Long) {
            // Set the visibility to VISIBLE
            gifImageView.visibility = View.VISIBLE

            // Create a Handler object
            val handler = Handler()

            // Post a delayed runnable to hide the GifImageView after the specified delay time
            handler.postDelayed({
                gifImageView.visibility = View.GONE
            }, delayTime)
        }
        val worker1 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_1)
        val worker2 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_2)
        val worker3 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_3)
        val worker4 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_4)
        val worker5 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_5)
        val worker6 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_6)
        val worker7 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_7) //glass
        val worker8 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_8) //plastic
        val worker9 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_9) //ore
        val worker10 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_10) //wood
        val worker11 = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.worker_11) //stone
        // Hide the gif at the beginning
        worker1.visibility = View.GONE
        worker2.visibility = View.GONE
        worker3.visibility = View.GONE
        worker4.visibility = View.GONE
        worker5.visibility = View.GONE
        worker6.visibility = View.GONE
        worker7.visibility = View.GONE //glass
        worker8.visibility = View.GONE //plastic
        worker9.visibility = View.GONE //ore
        worker10.visibility = View.GONE //wood
        worker11.visibility = View.GONE //stone

        //show dialogBox
        // 6x crafting bench (Inside dialogbox)
        // Produce crafting buttons
        // coffee table -> 1; folding chair -> 2; glass table -> 3; park bench -> 4; plastic chair -> 5; stone bench -> 6; stone table -> 7
        fun showDialogBox(callback: () -> Unit){
            val dialogBinding = layoutInflater.inflate(R.layout.dialogbox, null)
            val dialogBox = Dialog(this)
            dialogBox.setContentView(dialogBinding)
            dialogBox.setCancelable(true)
            dialogBox.setCanceledOnTouchOutside(true)
            dialogBox.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBox.show()
            var craftCoffeeTableButton = dialogBinding.findViewById<Button>(R.id.coffeeTable)
            var craftFoldingChairButton = dialogBinding.findViewById<Button>(R.id.foldingChair)
            var craftGlassTableButton = dialogBinding.findViewById<Button>(R.id.glassTable)
            var craftParkBenchButton = dialogBinding.findViewById<Button>(R.id.parkBench)
            var craftPlasticChairButton = dialogBinding.findViewById<Button>(R.id.plasticChair)
            var craftStoneBenchButton = dialogBinding.findViewById<Button>(R.id.stoneBench)
            var craftStoneTable = dialogBinding.findViewById<Button>(R.id.stoneTable)
            var closeDialogButton = dialogBinding.findViewById<Button>(R.id.closeDialogbox)

            //boolean var
            craftCoffeeTableButton.setOnClickListener {
                game.craftOrder(1)
                callback()
                dialogBox.dismiss()
            }
            craftFoldingChairButton.setOnClickListener {
                game.craftOrder(2)
                callback()
                dialogBox.dismiss()
            }
            craftGlassTableButton.setOnClickListener {
                game.craftOrder(3)
                callback()
                dialogBox.dismiss()
            }
            craftParkBenchButton.setOnClickListener {
                game.craftOrder(4)
                callback()
                dialogBox.dismiss()
            }
            craftPlasticChairButton.setOnClickListener {
                game.craftOrder(5)
                callback()
                dialogBox.dismiss()
            }
            craftStoneBenchButton.setOnClickListener {
                game.craftOrder(6)
                callback()
                dialogBox.dismiss()
            }
            craftStoneTable.setOnClickListener {
                game.craftOrder(7)
                callback()
                dialogBox.dismiss()
            }
            closeDialogButton.setOnClickListener {
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
            showDialogBox{
                showGifImageView(worker1, 5000L)
            }
        }
        craft_2.setOnClickListener{
            showDialogBox{
                showGifImageView(worker2, 5000L)
            }
        }
        craft_3.setOnClickListener{
            showDialogBox{
                showGifImageView(worker3, 5000L)
            }
        }
        craft_4.setOnClickListener{
            showDialogBox{
                showGifImageView(worker4, 5000L)
            }
        }
        craft_5.setOnClickListener{
            showDialogBox{
                showGifImageView(worker5, 5000L)
            }
        }
        craft_6.setOnClickListener{
            showDialogBox{
                showGifImageView(worker6, 5000L)
            }
        }

        // 5x resource sites
        // Wood -> 1; Stone -> 2; Glass -> 3; Ore -> 4; Plastic -> 5
        woodStationButton = findViewById(R.id.forest)
        woodStationButton.setOnClickListener {
            game.extractResource(1)
            showGifImageView(worker10, 1000L)
        }

        stoneStationButton = findViewById(R.id.digsite)
        stoneStationButton.setOnClickListener {
            game.extractResource(2)
            showGifImageView(worker11, 1000L)
        }

        glassStationButton = findViewById(R.id.smelters)
        glassStationButton.setOnClickListener {
            game.extractResource(3)
            showGifImageView(worker7, 1000L)
        }

        oreStationButton = findViewById(R.id.mine)
        oreStationButton.setOnClickListener {
            game.extractResource(4)
            showGifImageView(worker9, 1000L)
        }

        plasticStationButton = findViewById(R.id.factory)
        plasticStationButton.setOnClickListener {
            game.extractResource(5)
            showGifImageView(worker8, 1000L)
        }
    }

    override fun onResume() {
        super.onResume()
        val orderArray = arrayOf(orderImageView1,orderImageView2,orderImageView3,orderImageView4,
                                orderImageView5,orderImageView6,orderImageView7,orderImageView8,
                                orderImageView9,orderImageView10)
        game = Game(this, this, workerCount, workersTextView, woodTextView,
            stoneTextView, glassTextView, oreTextView, plasticTextView, scoreTextView,
            orderArray, currentRequestList,resources, score) // Create a new game instance when returning to the activity
    }
}
