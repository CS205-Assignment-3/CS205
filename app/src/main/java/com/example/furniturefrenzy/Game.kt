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
    private val scoreTextView: TextView,
    private val workersTextView: TextView
) {
    private val score = AtomicInteger(0)
    private val poolSize = 2
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(poolSize) // Change the number of workers here
    private val availableWorkers = Semaphore(poolSize)

    fun incrementScore() {
        if (availableWorkers.tryAcquire()) {
            updateWorkersTextView()
            executor.schedule({
                val newScore = score.incrementAndGet()
                activity.runOnUiThread {
                    updateScoreTextView(newScore)
                    availableWorkers.release()
                    updateWorkersTextView()
                }
            }, 3, TimeUnit.SECONDS)
        } else {
            // Show a message or update the UI to indicate that all workers are busy
        }
    }

    private fun updateScoreTextView(newScore: Int) {
        scoreTextView.text = "Score: $newScore"
    }

    private fun updateWorkersTextView() {
        val available = availableWorkers.availablePermits()
        workersTextView.text = "$available/$poolSize"
    }

    fun showGameOverScreen() {
        reset() // Reset the game state before showing the Game Over screen
        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("SCORE", score.get())
        context.startActivity(intent)
    }

    fun reset() {
        updateScoreTextView(0)
        // Cancel any pending tasks in the executor
        executor.shutdownNow()
    }
}