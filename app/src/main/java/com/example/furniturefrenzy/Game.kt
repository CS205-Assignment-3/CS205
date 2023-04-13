import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.example.furniturefrenzy.GameActivity
import com.example.furniturefrenzy.GameOverActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


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
) {
    // Game resources
    private val score = AtomicInteger(0)
    private val resources = ConcurrentHashMap<String, AtomicInteger>()
    init {
        // Initialize resources with their initial quantities
        resources["Logs"] = AtomicInteger(0)
        resources["Stone"] = AtomicInteger(0)
        resources["Ore"] = AtomicInteger(0)
        resources["PlasticRods"] = AtomicInteger(0)
        resources["Glass"] = AtomicInteger(0)
    }
    data class finalResource(val name: String, val requiredResource: List<Int>)
    private val finalResourceList = ArrayList<finalResource>()
    init {
        // Final Resource Info
        // Index is logs, stone, ore, plasticrods, glass

        finalResourceList.add(finalResource("PlasticChair", listOf(0, 0, 0, 5, 0)))
        finalResourceList.add(finalResource("FoldingChair", listOf(0, 0, 5, 0, 0)))
        finalResourceList.add(finalResource("StoneBench", listOf(0, 5, 0, 0, 0)))
        finalResourceList.add(finalResource("StoneTable", listOf(0, 10, 0, 0, 0)))
        finalResourceList.add(finalResource("ParkBench", listOf(5, 0, 5, 0, 0)))
        finalResourceList.add(finalResource("CoffeeTable", listOf(6, 0, 0, 0, 4)))
        finalResourceList.add(finalResource("DiningTable", listOf(0, 0, 2, 0, 8)))
    }
    // Add a ReentrantLock for atomic resource updates
    private val resourceLock = ReentrantLock()

    // Orders arrays
    private val currentRequestList = ArrayList<finalResource>()
    // Instantiate thread pool and semaphore for tracking
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(workerCount)
    private val gameExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val availableWorkers = Semaphore(workerCount)
    private val requestArrayLock = Semaphore(1)
    private var continueGame = true
    // TODO: Main game loop
    // TODO: Run another thread to check if order + grace period have exceeded (showGameOverScreen())
    // Add to the order arrays, track the orders needs to be fulfilled, handle gameover cirteria
    // loop...
    // showGameOverScreen()

    fun startGame(){
        gameExecutor.execute{
            val initialRequestInterval = 10000L // x: requests come in every 10 seconds at start
            val intervalReduction = 5000L // z: subtract 5 second from the request interval
            val intervalFloor = 3000L // the minimum request interval : 3 seconds
            val reductionTime = 3000L // y: after 3 seconds, requests will come in faster
            val startTime = System.currentTimeMillis()
            var currentRequestInterval = initialRequestInterval
            // At game start
            while (continueGame) {
                Thread.sleep(currentRequestInterval)

                // Request Timing
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime > reductionTime) {
                    currentRequestInterval = currentRequestInterval - intervalReduction
                    println("Reducing request interval")
                    if (currentRequestInterval < intervalFloor) {
                        currentRequestInterval = intervalFloor
                    }
                }

                // Random Request
                if(currentRequestList.size <= 9){
                    //TODO: Should this be .acquire instead? with try, if the lock is currently occupied, it will not wait and get continue
                    if (requestArrayLock.tryAcquire()){
                        val randomRequest = finalResourceList.random()
                        currentRequestList.add(randomRequest)
                        //queue1TextView.setImageResource(R.drawable.plastic_chair)
                        println("Added " + randomRequest)
                        requestArrayLock.release()
                    }
                }
                else{
                    println("GAME OVER")
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
            }, 3, TimeUnit.SECONDS)
        } else {
            // TODO: Show a message or update the UI to indicate that all workers are busy
        }
    }


    // TODO: Get type of job to be done, animate accordingly and assign job accordingly
    // Index 1 = plastic chair, 2 = folding chair, 3 =stone bench, 4 = stone table, 5 = park bench, 6 = coffee table, 7 = dining table
    //CONSUMER
    fun craftOrder(orderType: Int) {
        if (availableWorkers.tryAcquire()) {
            // Update available workers
            updateWorkersTextView()

            // Schedule the task
            executor.schedule({

                var craftingSuccessful = false

                requestArrayLock.acquire()
                val request = finalResourceList.getOrNull(orderType - 1)
                if (request != null && currentRequestList.contains(request)) {

                    // Check if there are enough resources to craft the product
                    resourceLock.withLock {
                        val canCraft = request.requiredResource.withIndex().all { (index, requiredAmount) ->
                            val resourceName = when (index) {
                                0 -> "Logs"
                                1 -> "Stone"
                                2 -> "Ore"
                                3 -> "PlasticRods"
                                4 -> "Glass"
                                else -> null
                            }
                            resourceName?.let { resources[it]?.get() ?: 0 >= requiredAmount } ?: false
                        }

                        if (canCraft) {
                            // Consume the required resources
                            request.requiredResource.forEachIndexed { index, requiredAmount ->
                                val resourceName = when (index) {
                                    0 -> "Logs"
                                    1 -> "Stone"
                                    2 -> "Ore"
                                    3 -> "PlasticRods"
                                    4 -> "Glass"
                                    else -> null
                                }
                                resourceName?.let { resources[it]?.addAndGet(-requiredAmount) }
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
                            currentRequestList.remove(request)

                            craftingSuccessful = true
                        }
                    }
                }
                requestArrayLock.release()

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
            }, 10, TimeUnit.SECONDS)
        } else {
            // TODO: Show a message or update the UI to indicate that all workers are busy
        }
    }



    private fun updateScoreTextView(newScore: Int) {
        scoreTextView.text = "Fulfilled: $newScore"
    }

    private fun updateWorkersTextView() {
        val available = availableWorkers.availablePermits()
        workersTextView.text = "$available/$workerCount"
    }

    fun showGameOverScreen() {
        reset() // Reset the game state before showing the Game Over screen
        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("score", score.get())
        intent.putExtra("timeTaken", 60000) // Time in milliseconds
        context.startActivity(intent)
    }

    fun reset() {
        updateScoreTextView(0)
        // Cancel any pending tasks in the executor
        executor.shutdownNow()
        gameExecutor.shutdownNow()
    }
}