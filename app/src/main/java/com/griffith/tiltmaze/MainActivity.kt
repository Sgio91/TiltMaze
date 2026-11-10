// Sergio Alves Da Silva Student Number: 3139115
package com.griffith.tiltmaze


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// MAIN ACTIVITY
// This is the first Activity launched when the app opens.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent tells Android what Compose UI to display.
        setContent {

            // Call our custom MainMenuScreen function.
            MainMenuScreen(

                // This function runs when Start Game is pressed.
                onStartGame = {

                    // Create an Explicit Intent.
                    // We specify exactly which Activity to open.
                    val intent = Intent(
                        this,
                        GameActivity::class.java
                    )

                    // Ask Android to open GameActivity(Not created yet).
                    startActivity(intent)
                }
            )
        }
    }
}


// @Composable means this function creates UI.
// We use it to build our Main Menu.
@Composable
fun MainMenuScreen(

    // onStartGame is a function passed from MainActivity.
    // The Main Menu calls it when the user presses Start.
    onStartGame: () -> Unit
) {

    // This variable controls the Instructions dialog.
    // false = hidden, true = visible.
    var showInstructions by remember {
        mutableStateOf(false)
    }

    // This variable controls the High Scores dialog.
    var showScores by remember {
        mutableStateOf(false)
    }

    // Surface is the background container for the screen.
    Surface(

        // fillMaxSize() makes it cover the whole screen.
        modifier = Modifier.fillMaxSize(),

        // Set a dark blue background.
        color = Color(0xFF14213D)
    ) {

        // Column arranges elements vertically.
        Column(

            modifier = Modifier
                // Use all available screen space.
                .fillMaxSize()

                // Add 24dp spacing inside the screen.
                .padding(24.dp),

            // Place items in the center vertically.
            verticalArrangement = Arrangement.Center,

            // Place items in the center horizontally.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // GAME TITLE.
            Text(
                text = "TILT MAZE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Spacer adds empty space between elements.
            Spacer(modifier = Modifier.height(12.dp))

            // SUBTITLE.
            Text(
                text = "Find your way to the goal!",
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // START GAME BUTTON.
            Button(

                // Call the function passed by MainActivity.
                onClick = onStartGame,

                // Make the button as wide as its container.
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("START GAME")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INSTRUCTIONS BUTTON.
            Button(

                // Change state to true when pressed.
                // Compose will automatically show the dialog.
                onClick = {
                    showInstructions = true
                },

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("INSTRUCTIONS")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HIGH SCORES BUTTON.
            Button(

                // Show High Scores dialog.
                onClick = {
                    showScores = true
                },

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("HIGH SCORES")
            }
        }
    }


    // INSTRUCTIONS DIALOG
    // This block only runs when showInstructions is true.
    if (showInstructions) {

        AlertDialog(

            // Close the dialog if the user taps outside it.
            onDismissRequest = {
                showInstructions = false
            },

            // Dialog title.
            title = {
                Text("How to Play")
            },

            // Dialog message.
            text = {
                Text(
                    "Guide the ball through the maze " +
                            "and reach the goal.\n\n" +
                            "Use the directional " +
                            "buttons to move the ball,till the end of the maze."
                )
            },

            // OK button closes the dialog.
            confirmButton = {
                TextButton(
                    onClick = {
                        showInstructions = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }


    // HIGH SCORES DIALOG.
    if (showScores) {

        AlertDialog(

            onDismissRequest = {
                showScores = false
            },

            title = {
                Text("High Scores")
            },

            text = {
                Text(
                    "Your saved best times will " +
                            "appear here!"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showScores = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
