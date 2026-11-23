// Sergio Alves Da Silva Student Number: 3139115

package com.griffith.tiltmaze

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale


// It receives the time from GameActivity, shows the result and allows sharing.
class ResultsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameTime = intent.getDoubleExtra( // Get data from GameActivity.
            "GAME_TIME", 0.0
        )

        // Display the compose interface.
        setContent {

            ResultsScreen( //  Call the custom ResultScreen function.

                // Pass the completion time to the UI.
                gameTime = gameTime,

                // Play again Button.
                onReplay = {
                    val replayIntent = Intent(
                        this, GameActivity::class.java
                    )

                    // Start a new game Activity.
                    startActivity(replayIntent)

                    // Close the current ResultsActivity.
                    finish()
                },

                // Main menu button.
                onHome = {
                    val homeIntent = Intent(
                        this, MainActivity::class.java
                    )

                    // Reuse the existing MainActivity and avoid creating another MainActivity.
                    homeIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(homeIntent)
                    finish()
                },

                // Share result button.
                onShare = {

                    val formattedTime = String.format(
                        Locale.US, "%.1f seconds", gameTime
                    )

                    val message = "You completed the challenge in $formattedTime!"

                    val shareIntent = Intent(
                        Intent.ACTION_SEND
                    ).apply {
                        type = "text/plain"

                        putExtra(
                            Intent.EXTRA_TEXT, message
                        )
                    }

                    // Sharing Menu
                    startActivity(
                        Intent.createChooser(
                            shareIntent, "Share Result"
                        )
                    )
                })
        }
    }
}


@Composable
fun ResultsScreen( // Results Screen UI interface.

    gameTime: Double,

    onReplay: () -> Unit,

    onHome: () -> Unit,

    onShare: () -> Unit

) {

    // New Layout colors.
    val forestGreen = Color(0xFF172C23)
    val templeGold = Color(0xFFE7C675)
    val parchment = Color(0xFFF4E9C8)
    val lightGreen = Color(0xFFB9CDB5)
    val stoneGreen = Color(0xFF344C3C)

    // Acts as the background container.
    Surface(
        modifier = Modifier.fillMaxSize(), color = forestGreen
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Decorative fantasy symbol.
            Text(
                text = "\uD83C\uDFC6", fontSize = 54.sp, color = templeGold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text( // Screen title.
                text = "VICTORY!", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = parchment
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // New style subtitle.
            Text(
                text = "THE MAZE IS CONQUERED",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = templeGold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Description displayed after completing the maze.
            Text(
                text = "The magical orb has reached " + "the golden portal.",
                fontSize = 15.sp,
                color = lightGreen,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // New style completion time panel.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = stoneGreen,
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(
                    width = 1.dp, color = templeGold
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "YOUR COMPLETION TIME",
                        fontSize = 13.sp,
                        color = lightGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    // Display Completion Time.
                    Text(
                        text = String.format(
                            Locale.US, "%.1f seconds", gameTime
                        ),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = templeGold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // Play again button.
            Button(
                onClick = onReplay, modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = templeGold, contentColor = forestGreen
                )
            ) {
                Text(
                    text = "PLAY AGAIN", fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Share result button.
            OutlinedButton(
                onClick = onShare, modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = parchment
                ),

                border = BorderStroke(
                    width = 1.dp, color = templeGold
                )

            ) {
                Text("SHARE YOUR VICTORY")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            //Main Menu button.
            OutlinedButton(
                onClick = onHome, modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = lightGreen
                ),

                border = BorderStroke(
                    width = 1.dp, color = lightGreen
                )

            ) {
                Text("BACK TO MENU")
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // Decorative footer message.
            Text(
                text = "ANOTHER ADVENTURE AWAITS!",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = lightGreen,
                textAlign = TextAlign.Center
            )
        }
    }
}
