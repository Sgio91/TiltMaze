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

                    val formattedTime = String.format(Locale.US, "%.1f seconds", gameTime)

                    val message = "You completed the Tilt Maze demo in $formattedTime!"

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
                }


            )
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
    // Acts as the background container.
    Surface(
        modifier = Modifier.fillMaxSize(),

        color = Color(0xFF14213D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text( // Screen title.
                text = "DEMO COMPLETE!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Elapsed Time", fontSize = 18.sp, color = Color.LightGray
            )

            // Display Completion Time.
            Text(

                text = String.format(
                    Locale.US, "%.1f seconds", gameTime
                ),

                fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            // Play again button.

            Button(

                onClick = onReplay, modifier = Modifier.fillMaxWidth()
            ) {
                Text("PLAY AGAIN")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Share result button.
            Button(
                onClick = onShare, modifier = Modifier.fillMaxWidth()
            ) {
                Text("SHARE RESULT")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            //Main Menu button.
            OutlinedButton(
                onClick = onHome, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White
                )

            ) {
                Text("MAIN MENU")
            }
        }
    }

}
