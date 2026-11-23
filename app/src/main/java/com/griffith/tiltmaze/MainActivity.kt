// Sergio Alves Da Silva Student Number: 3139115
package com.griffith.tiltmaze


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
                        this, GameActivity::class.java
                    )

                    // Ask Android to open GameActivity(Not created yet).
                    startActivity(intent)
                })
        }
    }
}


// We use it to build our Main Menu.
@Composable
fun MainMenuScreen(

    // onStartGame is a function passed from MainActivity.
    // The Main Menu calls it when the user presses Start.
    onStartGame: () -> Unit
) {

    // This variable controls the Instructions dialog.
    var showInstructions by remember {
        mutableStateOf(false)
    }

    // This variable controls the High Scores dialog.
    var showScores by remember {
        mutableStateOf(false)
    }

    // Inspired in Zelda games colors.
    val forestGreen = Color(0xFF172C23)
    val templeGold = Color(0xFFE7C675)
    val parchment = Color(0xFFF4E9C8)
    val lightGreen = Color(0xFFB9CDB5)


    // Surface is the background container for the screen.
    Surface(

        // fillMaxSize() makes it cover the whole screen.
        modifier = Modifier.fillMaxSize(), color = forestGreen
    ) {

        // Column arranges elements vertically.
        Column(

            modifier = Modifier
                // Use all available screen space.
                .fillMaxSize()


                .padding(28.dp),

            // Place items in the center vertically.
            verticalArrangement = Arrangement.Center,

            // Place items in the center horizontally.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "\u2726", fontSize = 50.sp, color = templeGold
            )


            // Spacer adds empty space between elements.
            Spacer(modifier = Modifier.height(12.dp))

            // GAME TITLE.
            Text(
                text = "TILT MAZE",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = parchment
            )


            // SUBTITLE.
            Text(
                text = "THE LOST WOOD", fontSize = 14.sp, letterSpacing = 2.sp, color = templeGold

            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Guide the magical orb through " + "the ancient labyrinth. ",
                fontSize = 15.sp,
                color = lightGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // START GAME BUTTON.
            Button(

                // Call the function passed by MainActivity.
                onClick = onStartGame,

                // Make the button as wide as its container.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = templeGold, contentColor = forestGreen
                )
            ) {
                Text("BEGIN ADVENTURE", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INSTRUCTIONS BUTTON.
            Button(

                // Change state to true when pressed.
                // Compose will automatically show the dialog.
                onClick = {
                    showInstructions = true
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = parchment
                ),

                border = BorderStroke(
                    width = 1.dp, color = templeGold

                )
            ) {
                Text("HOW TO PLAY")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HIGH SCORES BUTTON.
            OutlinedButton(

                // Show High Scores dialog.
                onClick = {
                    showScores = true
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = parchment
                ),

                border = BorderStroke(
                    width = 1.dp, color = templeGold
                )
            ) {
                Text("HALL OF RECORDS")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Decorative message at the bottom.
            Text(
                text = "An Ancient Challenge Awaits!",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = lightGreen,
                textAlign = TextAlign.Center
            )
        }
    }


    // Instructions dialog.
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
                    "Guide the magical orb to the golden portal.\n\n" + "Tilt your phone to move the orb through " + "the ancient maze.\n\n" + "Avoid the stone walls and reach the " + "portal a quickly as possible.\n\n" + "Use PAUSE to stop the game and " + "Restart to begin again if you are not ready yet."
                )
            },

            // Close button closes the dialog.
            confirmButton = {
                TextButton(
                    onClick = {
                        showInstructions = false
                    }) {
                    Text("CLOSE")
                }
            })
    }


    // High scores dialog.
    if (showScores) {

        AlertDialog(

            onDismissRequest = {
                showScores = false
            },

            title = {
                Text("Hall of Records")
            },

            text = {
                Text(
                    "Your best completion times will " + "appear here in the future!"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showScores = false
                    }) {
                    Text("CLOSE")
                }
            })
    }
}
