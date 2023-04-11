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

class Game(
    private val context: Context,
    private val activity: GameActivity,
    private val workerCount: Int,
    private val scoreTextView: TextView,
    private val workersTextView: TextView,
) {
    // Game resources
    private val score = AtomicInteger(0)
    // Orders arrays

    // Instantiate thread pool and semaphore for tracking
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(workerCount)
    private val availableWorkers = Semaphore(workerCount)

    // TODO: Main game loop
    // TODO: Run another thread to check if order + grace period have exceeded (showGameOverScreen())
    // Add to the order arrays, track the orders needs to be fulfilled, handle gameover cirteria
    // loop...
    // showGameOverScreen()

    // TODO: Same as craft but is producer
    fun extractResource(materialType: Int) {

    }

    // TODO: Get type of job to be done, animate accordingly and assign job accordingly
    //CONSUMER
    fun craftOrder(orderType: Int) {
        if (availableWorkers.tryAcquire()) { //Reduce semaphore
            // Update avail workers
            updateWorkersTextView()

            // Schedule the task
            executor.schedule({

                //Check if have materials and requirement to craft the product
                val newScore = score.incrementAndGet()
                activity.runOnUiThread {
                    updateScoreTextView(newScore)
                    // Todo for the worker
                    // Consume from buffer, show the worker working, update the orders array, update the UI for the orders


                }
                // Release worker and update worker avail
                availableWorkers.release()
                updateWorkersTextView()
            }, 3, TimeUnit.SECONDS)
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
    }
}