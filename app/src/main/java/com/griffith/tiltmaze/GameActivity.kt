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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.Color
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

    // Store whether the game is currently paused.
    private var isGamePaused = false

    // Store the dimensions used for collisions.
    private var mazeWidth = 0f
    private var mazeHeight = 0f

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

                onMazeSizeChanged = { width, height ->
                    // Update the dimensions used for collisions.
                    mazeWidth = width
                    mazeHeight = height
                },

                // Called when a direction button is pressed.
                onMove = { dx, dy ->
                    moveBall(dx, dy)
                },

                // Called when the player presses Pause or Resume.
                onPauseChanged = { paused ->

                    // Update the pause state.
                    isGamePaused = paused

                    if (paused) {
                        // Stop receiving sensor readings while paused.
                        sensorManager.unregisterListener(this)
                    } else {
                        // Reset sensor timing to avoid sudden movement.
                        lastSensorTime = 0L
                        smoothX = 0f
                        smoothY = 0f

                        // Start receiving accelerometer readings again.
                        accelerometer?.let { sensor ->
                            sensorManager.registerListener(
                                this,
                                sensor,
                                SensorManager.SENSOR_DELAY_GAME
                            )
                        }
                    }
                },

                // Called when the player presses Restart.
                onRestart = {

                    // Return the orb to its original position.
                    ballX = 0.20f
                    ballY = 0.22f

                    // Reset sensor smoothing.
                    lastSensorTime = 0L
                    smoothX = 0f
                    smoothY = 0f
                },

                onFinish = { seconds -> // Send the final time to ResultsActivity.
                    val intent = Intent(
                        this,
                        ResultsActivity::class.java
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
        if (!isGamePaused) {
            accelerometer?.let { sensor ->
                sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
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

        // Ignore accelerometer data while the game is paused.
        if (isGamePaused) {
            return
        }

        // Read acceleration along X and Y.
        val accelerationX = event.values[0]
        val accelerationY = event.values[1]

        // Apply a simple low-pass filter.
        smoothX = smoothX * 0.8f + accelerationX * 0.2f
        smoothY = smoothY * 0.8f + accelerationY * 0.2f

        // Calculate elapsed time between sensor readings and sensor timestamps are measured in nanoseconds.
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

        // Calculate movement using acceleration and time.
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

        // Do not move the orb while the game is paused.
        if (isGamePaused) {
            return
        }

        // Wait until we know the maze dimensions.
        if (mazeWidth <= 0f || mazeHeight <= 0f) {
            return
        }

        // Divide movement into small steps and this prevents fast movement from jumping through walls.
        val distanceX = kotlin.math.abs(dx * mazeWidth)
        val distanceY = kotlin.math.abs(dy * mazeHeight)

        val maxDistance = kotlin.math.max(
            distanceX,
            distanceY
        )

        // Each movement step is at most half the orb's radius.
        val maxStep = MazeLayout.BALL_RADIUS / 2f

        val steps = kotlin.math.ceil(
            maxDistance / maxStep
        ).toInt().coerceAtLeast(1)

        val stepX = dx / steps
        val stepY = dy / steps

        repeat(steps) {

            // Keep the orb inside the approximate game area.
            val nextX = (ballX + stepX)
                .coerceIn(0.12f, 0.88f)

            val nextY = (ballY + stepY)
                .coerceIn(0.14f, 0.86f)

            // Check horizontal movement first.
            if (
                MazeLayout.isPositionClear(
                    nextX,
                    ballY,
                    mazeWidth,
                    mazeHeight
                )
            ) {
                ballX = nextX
            }

            // Check vertical movement separately. This allows the orb to slide along a wall.
            if (
                MazeLayout.isPositionClear(
                    ballX,
                    nextY,
                    mazeWidth,
                    mazeHeight
                )
            ) {
                ballY = nextY
            }
        }
    }
}


@Composable // This composable draws the gameplay interface.
fun GameScreen(
    ballX: Float,
    ballY: Float,
    onMazeSizeChanged: (Float, Float) -> Unit,
    onMove: (Float, Float) -> Unit,
    onPauseChanged: (Boolean) -> Unit,
    onRestart: () -> Unit,
    onFinish: (Double) -> Unit, // Function that receives the elapsed time.
    onExit: () -> Unit
) {


// New style colors used throughout the Game Screen.
    val forestGreen = Color(0xFF172C23)
    val templeGold = Color(0xFFE7C675)
    val parchment = Color(0xFFF4E9C8)
    val lightGreen = Color(0xFFB9CDB5)
    val stoneGreen = Color(0xFF344C3C)

    // Record when the game begins.
    var startTime by remember {
        mutableLongStateOf(SystemClock.elapsedRealtime())
    }

    // Store elapsed game time in seconds.
    var elapsedSeconds by remember {
        mutableDoubleStateOf(0.0)
    }

    // Stores whether the game is paused.
    var isPaused by remember {
        mutableStateOf(false)
    }

    // Stores when the current pause started.
    var pausedAt by remember {
        mutableLongStateOf(0L)
    }

    // Stores the total time spent paused in milliseconds.
    var totalPausedTime by remember {
        mutableLongStateOf(0L)
    }

    // Store the actual maze size for goal detection.
    var currentMazeWidth by remember {
        mutableFloatStateOf(0f)
    }

    var currentMazeHeight by remember {
        mutableFloatStateOf(0f)
    }

    // Prevent the game from finishing more than once.
    var gameFinished by remember {
        mutableStateOf(false)
    }

    // LaunchedEffect starts a new routine.
    // It is canceled when this composable leaves the UI.
    LaunchedEffect(Unit) {

        // Continuously update the timer.
        while (true) {

            // Only update the timer while the game is running.
            if (!isPaused && !gameFinished) {

                val currentTime = SystemClock.elapsedRealtime()

                // Calculate how many seconds have passed since the game started.
                elapsedSeconds =
                    (currentTime - startTime - totalPausedTime)
                        .coerceAtLeast(0L) / 1000.0
            }

            // Update the timer every 100 milliseconds.
            delay(100.milliseconds)
        }
    }

    // Automatically detect when the orb reaches the goal.
    // This runs again when the ball's position changes.
    LaunchedEffect(
        ballX,
        ballY,
        currentMazeWidth,
        currentMazeHeight,
        isPaused
    ) {

        // Only check if the game has not finished.
        if (!gameFinished && !isPaused) {

            // Ask MazeLayout whether the orb touches the final point.
            val reachedGoal = MazeLayout.hasReachedGoal(
                ballX = ballX,
                ballY = ballY,
                width = currentMazeWidth,
                height = currentMazeHeight
            )

            if (reachedGoal) {

                // Prevent multiple finish events.
                gameFinished = true

                // Calculate the final completion time.
                val finalTime =
                    (SystemClock.elapsedRealtime() - startTime - totalPausedTime)
                        .coerceAtLeast(0L) / 1000.0

                // Open ResultsActivity automatically.
                onFinish(finalTime)
            }
        }
    }
// Provides a basic screen structure.
    Scaffold(
        // Dark forest background.
        containerColor = forestGreen,

        // Display the game title at the top.
        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Title and subtitle.
                Column {

                    Text(
                        text = "THE LOST WOOD",
                        color = templeGold,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "TILT MAZE",
                        color = parchment,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // Display elapsed time in a fantasy-style panel.
                Surface(
                    color = stoneGreen,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, templeGold)
                ) {

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "TIME",
                            color = lightGreen,
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = String.format(
                                Locale.US,
                                "%.1f s",
                                elapsedSeconds
                            ),
                            color = templeGold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
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

            // Custom Maze component.
            // The existing maze design and collision logic stay unchanged.
            MazeScreen(
                ballX = ballX,
                ballY = ballY,

                modifier = Modifier
                    .fillMaxWidth()

                    // Use the remaining vertical space.
                    .weight(1f)

                    // Measure the Canvas when its size changes.
                    .onSizeChanged { size ->

                        // Store the dimensions for goal detection.
                        currentMazeWidth = size.width.toFloat()
                        currentMazeHeight = size.height.toFloat()

                        // Send dimensions to GameActivity for wall collision detection.
                        onMazeSizeChanged(
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // UP button.
            Button(
                onClick = {
                    // Reduce Y to move upwards.
                    onMove(0f, -0.03f)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = stoneGreen,
                    contentColor = parchment
                )
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = stoneGreen,
                        contentColor = parchment
                    )
                ) {
                    Text("LEFT")
                }

                // DOWN button.
                Button(
                    onClick = {
                        // Increase Y to move down.
                        onMove(0f, 0.03f)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = stoneGreen,
                        contentColor = parchment
                    )
                ) {
                    Text("DOWN")
                }

                // RIGHT button.
                Button(
                    onClick = {
                        // Increase X to move right.
                        onMove(0.03f, 0f)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = stoneGreen,
                        contentColor = parchment
                    )
                ) {
                    Text("RIGHT")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pause and Restart controls.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Pause / Resume button.
                Button(
                    onClick = {

                        // Calculate the elapsed time when pressed.
                        val now = SystemClock.elapsedRealtime()

                        if (!isPaused) {

                            // Store when the pause starts.
                            pausedAt = now

                            // Freeze the displayed timer.
                            elapsedSeconds =
                                (now - startTime - totalPausedTime)
                                    .coerceAtLeast(0L) / 1000.0

                            // Pause the game.
                            isPaused = true
                            onPauseChanged(true)

                        } else {

                            // Add the paused duration to total pause time.
                            totalPausedTime += now - pausedAt

                            // Resume the game.
                            isPaused = false
                            onPauseChanged(false)
                        }
                    },
                    modifier = Modifier.weight(1f),

                    // Golden button matching the Main Menu.
                    colors = ButtonDefaults.buttonColors(
                        containerColor = templeGold,
                        contentColor = forestGreen
                    )
                ) {
                    Text(if (isPaused) "RESUME" else "PAUSE")
                }

                // Restart Button.
                OutlinedButton(
                    onClick = {

                        // Reset the game clock.
                        startTime = SystemClock.elapsedRealtime()
                        elapsedSeconds = 0.0

                        // Clear pause history.
                        pausedAt = 0L
                        totalPausedTime = 0L

                        // Reset the game state.
                        gameFinished = false
                        isPaused = false

                        // Return the orb to its starting position.
                        onRestart()

                        // Allow movement again.
                        onPauseChanged(false)
                    },
                    modifier = Modifier.weight(1f),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = parchment
                    ),

                    border = BorderStroke(
                        width = 1.dp,
                        color = templeGold
                    )
                ) {
                    Text("RESTART")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Back to menu button.
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = lightGreen
                ),

                border = BorderStroke(
                    width = 1.dp,
                    color = lightGreen
                )
            ) {
                Text("BACK TO MENU")
            }
        }
    }
}

