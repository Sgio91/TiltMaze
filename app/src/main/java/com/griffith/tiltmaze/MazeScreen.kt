
package com.griffith.tiltmaze

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// New design colors layout.
private val forestGreen = Color(0xFF273B2B)
private val stoneColour = Color(0xFF8C997D)
private val stoneShadow = Color(0xFF4B5F4D)
private val magicBlue = Color(0xFFA0F1E5)
private val templeGold = Color(0xFFE7C675)

@Composable
fun MazeScreen(
    ballX: Float,
    ballY: Float,
    modifier: Modifier = Modifier
) {

    // Canvas lets us draw the game using shapes.
    Canvas(
        modifier = modifier.background(forestGreen)
    ) {

        // Width and height of our drawing area.
        val w = size.width
        val h = size.height


         // Draw the floor.
        // Add subtle lines to resemble stone tiles.
        for (i in 1..9) {

            val x = w * i / 10f
            val y = h * i / 10f

            drawLine(
                color = stoneColour.copy(alpha = 0.12f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )

            drawLine(
                color = stoneColour.copy(alpha = 0.12f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Draw the outer stone walls.
        drawStoneWall(w, h, .08f, .10f, .92f, .12f)
        drawStoneWall(w, h, .08f, .88f, .92f, .90f)
        drawStoneWall(w, h, .08f, .10f, .10f, .90f)
        drawStoneWall(w, h, .90f, .10f, .92f, .90f)


        // Draw the internal walls.
        // Maze coordinates.
        drawStoneWall(w, h, .38f, .10f, .40f, .65f)
        drawStoneWall(w, h, .62f, .35f, .64f, .90f)


        // Draw the new goal.
        val goal = Offset(
            x = w * .80f,
            y = h * .78f
        )

        // Soft golden glow around the goal.
        drawCircle(
            color = templeGold.copy(alpha = 0.18f),
            radius = 28f,
            center = goal
        )

        // Golden outer ring.
        drawCircle(
            color = templeGold,
            radius = 17f,
            center = goal
        )

        // Dark center, giving a different appearance.
        drawCircle(
            color = forestGreen,
            radius = 11f,
            center = goal
        )

        // Golden center.
        drawCircle(
            color = templeGold,
            radius = 5f,
            center = goal
        )


        // Draw the new ball layout.
        // ballX and ballY come from GameActivity.
        val ballPosition = Offset(
            x = w * ballX,
            y = h * ballY
        )

        // Soft outer glow.
        drawCircle(
            color = magicBlue.copy(alpha = 0.20f),
            radius = 24f,
            center = ballPosition
        )

        // Main new ball.
        drawCircle(
            color = magicBlue,
            radius = 13f,
            center = ballPosition
        )

        // Small highlight makes it look more luminous.
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 4f,
            center = Offset(
                x = ballPosition.x - 4f,
                y = ballPosition.y - 4f
            )
        )
    }
}

// Reusable function to draw stone walls.
private fun DrawScope.drawStoneWall(
    w: Float,
    h: Float,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float
) {

    // Dark shadow behind the stone.
    drawRect(
        color = stoneShadow,
        topLeft = Offset(w * left, h * top),
        size = Size(
            w * (right - left),
            h * (bottom - top)
        )
    )

    // Lighter stone center.
    // This gives each wall a simple layered appearance.
    val inset = 2f

    val wallLeft = w * left
    val wallTop = h * top
    val wallWidth = w * (right - left)
    val wallHeight = h * (bottom - top)

    if (wallWidth > inset * 2 &&
        wallHeight > inset * 2) {

        drawRect(
            color = stoneColour,
            topLeft = Offset(
                wallLeft + inset,
                wallTop + inset
            ),
            size = Size(
                wallWidth - inset * 2,
                wallHeight - inset * 2
            )
        )
    }
}
