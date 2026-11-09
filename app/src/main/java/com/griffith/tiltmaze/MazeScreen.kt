package com.griffith.tiltmaze


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope


@Composable
fun MazeScreen( // This function creates the maze graphics.

    ballX: Float, // Horizontal position of the ball.
    ballY: Float, // Vertical position of the ball.
    modifier: Modifier = Modifier // Modifier allows GameScreen to control the canvas size.

) {
    // Creating a Drawing area in pixels.
    Canvas(
        modifier = modifier.background(Color(0xFFF5F7FA))
    ) {

        val w = size.width
        val h = size.height
        val wallColor = Color(0xFF233047)

        // Outer maze walls.
        drawWall( // Top wall.
            w, h, 0.08f, 0.10f, 0.92f, 0.12f, wallColor
        )

        drawWall( // Bottom wall.
            w, h, 0.08f, 0.88f, 0.92f, 0.90f, wallColor
        )

        drawWall( // Left wall.
            w, h, 0.08f, 0.10f, 0.10f, 0.90f, wallColor
        )

        drawWall( // Right wall.
            w, h, 0.90f, 0.10f, 0.92f, 0.90f, wallColor
        )

        // Internal maze walls.

        drawWall( // First internal wall.
            w, h, 0.38f, 0.10f, 0.40f, 0.65f, wallColor
        )

        drawWall( // Second internal wall.
            w, h, 0.62f, 0.35f, 0.64f, 0.90f, wallColor
        )

        // Drawing the final goal.

        drawCircle(

            color = Color(0xFF29B66F), radius = 16f, center = Offset(
                x = w * 0.80f, y = h * 0.78f
            )
        )

        // Drawing the player ball.
        drawCircle(

            color = Color(0xFF2878F0), radius = 16f, center = Offset(
                x = w * ballX, y = h * ballY
            )

        )


    }
}

// Helper Function.
// This Function voids repeating drawRectangle code.
private fun DrawScope.drawWall(

    // Canvas width and height.
    w: Float, h: Float,

    // Wall boundaries.
    left: Float, top: Float, right: Float, bottom: Float,

    // Wall colour.
    color: Color

) {
    // Draw a rectangle representing one wall.
    drawRect(

        color = color,

        // Where the rectangle starts.
        topLeft = Offset(
            x = w * left, y = h * top
        ),

        size = Size(
            width = w * (right - left), height = h * (bottom - top)
        )

    )

}