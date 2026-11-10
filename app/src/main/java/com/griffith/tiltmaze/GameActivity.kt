// Sergio Alves Da Silva Student Number: 3139115

package com.griffith.tiltmaze

import android.content.Intent
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


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            GameScreen(onFinish = { seconds -> // Called when the player finishes the demo.
                val intent = Intent(
                    this, ResultsActivity::class.java // Creating ResultsActivity next.
                )

                intent.putExtra(
                    "GAME_TIME", seconds
                ) // Pass the completion time to ResultsActivity.

                startActivity(intent) // Open the results screen.
                finish() // Close this GameActivity.
            }, onExit = {
                finish()
            })
        }
    }
}

@Composable // This composable draws the gameplay interface.
fun GameScreen(
    onFinish: (Double) -> Unit, // Function that receives the elapsed time.
    onExit: () -> Unit
) {
    var ballX by remember { // Horizontal ball position.
        mutableFloatStateOf(0.20f)
    }
    var ballY by remember { // Vertical ball position.
        mutableFloatStateOf(0.22f)
    }
    val startTime = remember { // Record the time when the GameScreen starts.
        SystemClock.elapsedRealtime()
    }
    var elapsedSeconds by remember { // Store how many seconds have passed.
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
                ballX = ballX, ballY = ballY,

                modifier = Modifier
                    .fillMaxWidth()

                    // Use the remaining vertical space.
                    .weight(1f)
            )

            // UP button.
            Button(
                onClick = {
                    // Reduce Y to move upwards.
                    ballY = (ballY - 0.03f).coerceIn(0.14f, 0.86f)
                }) {
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
                        ballX = (ballX - 0.03f).coerceIn(0.12f, 0.88f)
                    }) {
                    Text("LEFT")
                }

                // DOWN button.
                Button(
                    onClick = {
                        // Increase Y to move down.
                        ballY = (ballY + 0.03f).coerceIn(0.14f, 0.86f)
                    }) {
                    Text("DOWN")
                }

                // RIGHT button.
                Button(
                    onClick = {
                        // Increase X to move right.
                        ballX = (ballX + 0.03f).coerceIn(0.12f, 0.88f)
                    }) {
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
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("FINISH GAME")
            }

            // Back to menu button.
            OutlinedButton(
                onClick = onExit, modifier = Modifier.fillMaxWidth()
            ) {
                Text("BACK TO MENU")
            }
        }
    }
}

