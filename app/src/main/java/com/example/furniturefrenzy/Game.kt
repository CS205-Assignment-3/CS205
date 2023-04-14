import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.example.furniturefrenzy.GameActivity
import com.example.furniturefrenzy.GameOverActivity
import com.example.furniturefrenzy.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import com.example.furniturefrenzy.GameActivity.finalResource


class Game(
    private val context: Context,
    private val activity: GameActivity,
    private val workerCount: Int,
    private val workersTextView: TextView,
    private val woodTextView: TextView,
    private val stoneTextView: TextView,
    private val glassTextView: TextView,
    private val oreTextView: TextView,
    private val plasticTextView: TextView,
    private val scoreTextView: TextView,
    private val orderArray: Array<ImageView>,
    private val currentRequestList: ArrayList<finalResource>,
    private val resources: ConcurrentHashMap<String, AtomicInteger>,
    private val score: AtomicInteger
) {
    // Game resources
    private val finalResourceList = ArrayList<finalResource>()
    init {
        // Final Resource Info
        // Index is logs, stone, ore, plastic Rods, glass
        finalResourceList.add(finalResource("CoffeeTable", listOf(6, 0, 0, 0, 4)))
        finalResourceList.add(finalResource("FoldingChair", listOf(0, 0, 5, 0, 0)))
        finalResourceList.add(finalResource("DiningTable", listOf(0, 0, 2, 0, 8)))
        finalResourceList.add(finalResource("ParkBench", listOf(5, 0, 5, 0, 0)))
        finalResourceList.add(finalResource("PlasticChair", listOf(0, 0, 0, 5, 0)))
        finalResourceList.add(finalResource("StoneBench", listOf(0, 5, 0, 0, 0)))
        finalResourceList.add(finalResource("StoneTable", listOf(0, 10, 0, 0, 0)))
    }

    // Add a ReentrantLock for atomic resource updates
    private val resourceLock = ReentrantLock()
    // sharedPreference to check whether to start the game
    private val sharedPreferences = context.getSharedPreferences("continueGame",Context.MODE_PRIVATE)
    // Instantiate thread pool and semaphore for tracking
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(workerCount)
    private val gameExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val availableWorkers = Semaphore(workerCount)
    private val requestArrayLock = Semaphore(1)

    fun startGame(){
        gameExecutor.execute{
            val initialRequestInterval = 10000L // x: requests come in every 10 seconds at start
            val intervalReduction = 2000L // z: subtract 2 second from the request interval
            val intervalFloor = 5000L // the minimum request interval : 5 seconds
            val reductionTime = 5000L // y: after 5 seconds, requests will come in faster
            val startTime = System.currentTimeMillis()
            var currentRequestInterval = initialRequestInterval

            // At game start
            while (sharedPreferences.getBoolean("continueGame", true)) {
                Thread.sleep(currentRequestInterval)
                // Request Timing
                println("running")
                val elapsedTime = System.currentTimeMillis() - startTime

                if (elapsedTime > reductionTime) {
                    currentRequestInterval = currentRequestInterval - intervalReduction
                    if (currentRequestInterval < intervalFloor) {
                        currentRequestInterval = intervalFloor
                    }
                }
                // Random Request
                if(currentRequestList.size < 10){
                    requestArrayLock.acquire()
                    val randomRequest = finalResourceList.random()
                    currentRequestList.add(randomRequest)
                    showOrders()
                    requestArrayLock.release()
                }
                else{
                    activity.runOnUiThread{
                        showGameOverScreen(System.currentTimeMillis()-startTime)
                    }
                    break
                }
            }
        }
    }
    // TODO: Same as craft but is producer
    fun extractResource(resourceType: Int) {
        if (availableWorkers.tryAcquire()) {
            // Update available workers
            updateWorkersTextView()

            // Schedule the task
            executor.schedule({
                // Perform atomic update of the selected resource using ReentrantLock
                resourceLock.withLock {
                    when (resourceType) {
                        1 -> resources["Logs"]?.incrementAndGet()
                        2 -> resources["Stone"]?.incrementAndGet()
                        3 -> resources["Glass"]?.incrementAndGet()
                        4 -> resources["Ore"]?.incrementAndGet()
                        5 -> resources["PlasticRods"]?.incrementAndGet()
                        else -> {
                            println("Error. Only accept 1 -5")
                        }
                    }
                }

                // Update the UI with the new resource quantities
                activity.runOnUiThread {
                    // TODO: Update the UI elements to display the new resource quantities
                    when (resourceType) {
                        1 -> woodTextView.text = resources["Logs"]?.get().toString()
                        2 -> stoneTextView.text = resources["Stone"]?.get().toString()
                        3 -> glassTextView.text = resources["Glass"]?.get().toString()
                        4 -> oreTextView.text = resources["Ore"]?.get().toString()
                        5 -> plasticTextView.text = resources["PlasticRods"]?.get().toString()
                    }
                }
                // Release worker and update worker avail
                availableWorkers.release()
                updateWorkersTextView()
            }, 1, TimeUnit.SECONDS)
        } else {
        }
    }

    //CONSUMER
    fun craftOrder(orderType: Int) {
        if (availableWorkers.tryAcquire()) {
            // Update available workers
            updateWorkersTextView()

            // Schedule the task
            executor.schedule({
                var craftingSuccessful = false

                val request = finalResourceList.getOrNull(orderType - 1)
                if (request != null) {

                    // Check if there are enough resources to craft the product
                    resourceLock.withLock {
                        var canCraft = true
                        for (index in request.requiredResource.indices) {
                            val requiredAmount = request.requiredResource[index]
                            val resourceName = when (index) {
                                0 -> "Logs"
                                1 -> "Stone"
                                2 -> "Ore"
                                3 -> "PlasticRods"
                                4 -> "Glass"
                                else -> null
                            }
                            if (resourceName != null) {
                                val resourceAmount = resources[resourceName]?.get() ?: 0
                                if (resourceAmount < requiredAmount) {
                                    canCraft = false
                                    break
                                }
                            }
                        }
                        if (canCraft) {
                            // Consume the required resources
                            for (index in request.requiredResource.indices) {
                                val requiredAmount = request.requiredResource[index]
                                val resourceName = when (index) {
                                    0 -> "Logs"
                                    1 -> "Stone"
                                    2 -> "Ore"
                                    3 -> "PlasticRods"
                                    4 -> "Glass"
                                    else -> null
                                }
                                if (resourceName != null) {
                                    resources[resourceName]?.addAndGet(-requiredAmount)
                                }
                            }

                            // Update UI with new resource quantities
                            activity.runOnUiThread {
                                woodTextView.text = resources["Logs"]?.get().toString()
                                stoneTextView.text = resources["Stone"]?.get().toString()
                                glassTextView.text = resources["Glass"]?.get().toString()
                                oreTextView.text = resources["Ore"]?.get().toString()
                                plasticTextView.text = resources["PlasticRods"]?.get().toString()
                            }

                            // Remove the request from the currentRequestList
                            requestArrayLock.acquire()
                            if(currentRequestList.remove(request)){
                                craftingSuccessful = true
                            }
                            showOrders()
                            requestArrayLock.release()
                        }
                    }
                }
                if (craftingSuccessful) {
                    // Increment score and update score UI
                    val newScore = score.incrementAndGet()
                    activity.runOnUiThread {
                        updateScoreTextView(newScore)
                    }
                }
                // Release worker and update worker availability
                availableWorkers.release()
                updateWorkersTextView()
            }, 5, TimeUnit.SECONDS)
        } else {
        }
    }

    private fun updateScoreTextView(newScore: Int) {
        scoreTextView.text = "Fulfilled: $newScore"
    }

    private fun updateWorkersTextView() {
        val available = availableWorkers.availablePermits()
        workersTextView.text = "$available/$workerCount"
    }

    fun showGameOverScreen(timeTaken:Long) {
        reset() // Reset the game state before showing the Game Over screen
        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("score", score.get())
        intent.putExtra("timeTaken", timeTaken.toInt()) // Time in milliseconds
        context.startActivity(intent)
    }

    fun reset() {
        updateScoreTextView(0)
        // Cancel any pending tasks in the executor
        executor.shutdownNow()
        gameExecutor.shutdownNow()
    }

    fun showOrders(){
        for (i in 0..9){
            if (i < currentRequestList.size){
                when(currentRequestList[i].name.toString()){
                    "PlasticChair" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.plastic_chair)}
                    "FoldingChair" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.folding_chair)}
                    "StoneBench" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.stone_bench)}
                    "StoneTable" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.stone_table)}
                    "ParkBench" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.park_bench)}
                    "CoffeeTable" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.coffee_table)}
                    "DiningTable" -> activity.runOnUiThread{orderArray[i].setImageResource(R.drawable.glass_table)}
                }
            }
            else{
                activity.runOnUiThread{orderArray[i].setImageResource(android.R.color.transparent)}
            }
        }
    }
}