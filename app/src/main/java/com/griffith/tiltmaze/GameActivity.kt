// Sergio Alves Da Silva Student Number: 3139115
package com.griffith.tiltmaze

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds



// The GameActivity also implements SensorEventListener, this allows it to receive updates from the accelerometer.
class GameActivity : ComponentActivity(), SensorEventListener {

    // Used to access the phone's physical senors.
    private lateinit var sensorManager: SensorManager

    // Store the accelerometer sensor. It may be null if the device does not have one.
    private var accelerometer: Sensor? = null

    // Ball position.
    private var ballX by mutableFloatStateOf(0.20f)
    private var ballY by mutableFloatStateOf(0.22f)

    private var smoothX = 0f
    private var smoothY = 0f

    // Timestamp of the previous sensor reading.
    private var lastSensorTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the sensor manager.
        sensorManager = getSystemService(
            SENSOR_SERVICE
        ) as SensorManager

        // Find the accelerometer.
        accelerometer = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

        if (accelerometer == null) {
            Toast.makeText(
                this,
                "Accelerometer unavailable. Use the buttons.",
                Toast.LENGTH_LONG
            ).show()
        }

        // Display the game screen.
        setContent {
            GameScreen(
                ballX = ballX,
                ballY = ballY,

                // Called when a direction button is pressed.
                onMove = { dx, dy ->
                    moveBall(dx, dy)
                },

                onFinish = { seconds -> // Send the final time to ResultsActivity.
                    val intent = Intent(
                        this, ResultsActivity::class.java // Creating ResultsActivity next.
                    )

                    intent.putExtra(
                        "GAME_TIME", seconds
                    ) // Pass the completion time to ResultsActivity.

                    startActivity(intent) // Open the results screen.
                    finish() // Close this GameActivity.
                },

                onExit = {
                    finish()
                }
            )
        }
    }

    // Called when the activity becomes active.
    override fun onResume() {
        super.onResume()

        // Reset sensor timing and smoothing.
        lastSensorTime = 0L
        smoothX = 0f
        smoothY = 0f

        // Start receiving accelerometer readings.
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    // Called when the activity is no longer in the foreground.
    override fun onPause() {
        super.onPause()

        // Stop receiving sensor updates to save resources.
        sensorManager.unregisterListener(this)
    }

    // Android calls this whenever sensor data changes.
    override fun onSensorChanged(event: SensorEvent) {

        // Only process accelerometer events.
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) {
            return
        }

        // Read acceleration along X and Y.
        val accelerationX = event.values[0]
        val accelerationY = event.values[1]

        // Apply a simple low-pass filter.
        smoothX = smoothX * 0.8f + accelerationX * 0.2f
        smoothY = smoothY * 0.8f + accelerationY * 0.2f

        // Calculate elpsed time between sensor readings and sensor timestamps are measured in nanoseconds.
        val deltaTime = if (lastSensorTime == 0L) {
            0f
        } else {
            ((event.timestamp - lastSensorTime) / 1_000_000_000f)
                .coerceIn(0f, 0.05f)
        }

        lastSensorTime = event.timestamp

        // Ignore small movements to reduce jitter.
        val deadZone = 0.25f

        val tiltX = if (kotlin.math.abs(smoothX) > deadZone) {
            smoothX
        } else {
            0f
        }

        val tiltY = if (kotlin.math.abs(smoothY) > deadZone) {
            smoothY
        } else {
            0f
        }

        // Movement sensitivity and increase this value to move faster.
        val sensitivity = 0.055f

        // Calculate movement using acceleration and time
        val dx = -tiltX * sensitivity * deltaTime
        val dy = tiltY * sensitivity * deltaTime

        moveBall(dx, dy)
    }

    // Required by SensorEventListener.
    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {

    }

    // Called by the sensor and by the direction buttons.
    private fun moveBall(dx: Float, dy: Float) {

        // Keep the orb inside the approximate game area.
        ballX = (ballX + dx).coerceIn(0.12f, 0.88f)
        ballY = (ballY + dy).coerceIn(0.14f, 0.86f)
    }
}

@Composable // This composable draws the gameplay interface.
fun GameScreen(
    ballX: Float,
    ballY: Float,
    onMove: (Float, Float) -> Unit,
    onFinish: (Double) -> Unit, // Function that receives the elapsed time.
    onExit: () -> Unit
) {
    // Record when the game begins.
    val startTime = remember {
        SystemClock.elapsedRealtime()
    }

    // Store elapsed game time in seconds.
    var elapsedSeconds by remember {
        mutableDoubleStateOf(0.0)
    }

    // LaunchedEffect starts a new routine.
    // It is canceled when this composable leaves the UI.
    LaunchedEffect(Unit) {

        // Continuously update the timer.
        while (true) {
            elapsedSeconds = (SystemClock.elapsedRealtime() - startTime) / 1000.0
            delay(100.milliseconds)
        }
    }

    // Provides a basic screen structure.
    Scaffold(

        // Display the game title at the top.
        topBar = {
            Text(
                text = "TILT MAZE",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

    ) { innerPadding ->

        // Arranges the timer, maze and buttons vertically.
        Column(
            modifier = Modifier
                .fillMaxSize()

                // Respect the space used by Scaffold.
                .padding(innerPadding)

                // Add spacing around the content.
                .padding(12.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Timer text.
            Text(
                text = String.format(
                    Locale.US, "Time: %.1f seconds", elapsedSeconds
                )
            )

            // Custom Maze component.
            MazeScreen(
                ballX = ballX,
                ballY = ballY,

                modifier = Modifier
                    .fillMaxWidth()

                    // Use the remaining vertical space.
                    .weight(1f)
            )

            // UP button.
            Button(
                onClick = {
                    // Reduce Y to move upwards.
                    onMove(0f, -0.03f)
                }
            ) {
                Text("UP")
            }

            // ROW arranges buttons horizontally.
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // LEFT button.
                Button(
                    onClick = {
                        // Reduce X to move left.
                        onMove(-0.03f, 0f)
                    }
                ) {
                    Text("LEFT")
                }

                // DOWN button.
                Button(
                    onClick = {
                        // Increase Y to move down.
                        onMove(0f, 0.03f)
                    }
                ) {
                    Text("DOWN")
                }

                // RIGHT button.
                Button(
                    onClick = {
                        // Increase X to move right.
                        onMove(0.03f, 0f)
                    }
                ) {
                    Text("RIGHT")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Finish game button.
            Button(
                onClick = {
                    // Calculate the elapsed time when pressed.
                    val finalTime = (SystemClock.elapsedRealtime() - startTime) / 1000.0

                    // Send the result to GameActivity.
                    onFinish(finalTime)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("FINISH GAME")
            }

            // Back to menu button.
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("BACK TO MENU")
            }
        }
    }
}
