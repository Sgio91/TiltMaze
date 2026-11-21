// Sergio Alves Da Silva Student Number: 3139115

package com.griffith.tiltmaze

import kotlin.math.max

// Represents one rectangular wall in the maze.
data class MazeWall(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

// Stores the maze's structure and collision rules.
object MazeLayout {

    // The physical radius of our magical orb in pixels.
    const val BALL_RADIUS = 13f

    // Define all the maze walls in one place.
    val walls = listOf(

        // Outer walls.
        MazeWall(0.08f, 0.10f, 0.92f, 0.12f),
        MazeWall(0.08f, 0.88f, 0.92f, 0.90f),
        MazeWall(0.08f, 0.10f, 0.10f, 0.90f),
        MazeWall(0.90f, 0.10f, 0.92f, 0.90f),

        // First internal wall.
        MazeWall(0.38f, 0.10f, 0.40f, 0.65f),

        // Second internal wall.
        MazeWall(0.62f, 0.35f, 0.64f, 0.90f)
    )

    // Returns true when the orb can occupy this position.
    fun isPositionClear(
        ballX: Float,
        ballY: Float,
        width: Float,
        height: Float
    ): Boolean {

        // We need valid Canvas measurements.
        if (width <= 0f || height <= 0f) {
            return false
        }

        // Convert the orb's proportional position to pixels.
        val x = ballX * width
        val y = ballY * height

        // Prevent the orb from leaving the Canvas.
        if (
            x - BALL_RADIUS < 0f ||
            x + BALL_RADIUS > width ||
            y - BALL_RADIUS < 0f ||
            y + BALL_RADIUS > height
        ) {
            return false
        }

        // Check the orb against each wall.
        for (wall in walls) {

            // Convert the wall coordinates into pixels.
            val left = wall.left * width
            val top = wall.top * height
            val right = wall.right * width
            val bottom = wall.bottom * height

            // Find the closest point on the wall to the orb.
            val closestX = x.coerceIn(left, right)
            val closestY = y.coerceIn(top, bottom)

            // Calculate the distance along each axis.
            val distanceX = x - closestX
            val distanceY = y - closestY

            // The squared distance avoids needing a square root.
            val distanceSquared =
                distanceX * distanceX + distanceY * distanceY

            // If the distance is smaller than the orb's
            // radius, the orb is touching the wall.
            if (distanceSquared < BALL_RADIUS * BALL_RADIUS) {
                return false
            }
        }

        // No collision was found.
        return true
    }
}

