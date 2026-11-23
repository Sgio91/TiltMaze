// Sergio Alves Da Silva Student Number: 3139115

package com.griffith.tiltmaze

// Represents one rectangular wall in the maze.
data class MazeWall(
    val left: Float, val top: Float, val right: Float, val bottom: Float
)

// Stores the maze's structure and collision rules.
object MazeLayout {

    // The physical radius of our magical orb in pixels.
    const val BALL_RADIUS = 13f

    // The Final point position inside the maze.
    // These coordinates are proportional to the Canvas size.
    const val GOAL_X = 0.80f
    const val GOAL_Y = 0.78f

    // Radius of the final point in pixels.
    const val GOAL_RADIUS = 17f

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
        ballX: Float, ballY: Float, width: Float, height: Float
    ): Boolean {

        // We need valid Canvas measurements to calculate distance.
        if (width <= 0f || height <= 0f) {
            return false
        }

        // Convert the orb's proportional position to pixels.
        val x = ballX * width
        val y = ballY * height

        // Prevent the orb from leaving the Canvas.
        if (x - BALL_RADIUS < 0f || x + BALL_RADIUS > width || y - BALL_RADIUS < 0f || y + BALL_RADIUS > height) {
            return false
        }

        // Check the orb against each wall.
        for ((left1, top1, right1, bottom1) in walls) {

            // Convert the wall coordinates into pixels.
            val left = left1 * width
            val top = top1 * height
            val right = right1 * width
            val bottom = bottom1 * height

            // Find the closest point on the wall to the orb.
            val closestX = x.coerceIn(left, right)
            val closestY = y.coerceIn(top, bottom)

            // Calculate the distance along each axis.
            val distanceX = x - closestX
            val distanceY = y - closestY

            // The squared distance avoids needing a square root.
            val distanceSquared = distanceX * distanceX + distanceY * distanceY

            // If the distance is smaller than the orb's
            // radius, the orb is touching the wall.
            if (distanceSquared < BALL_RADIUS * BALL_RADIUS) {
                return false
            }
        }

        // No collision was found.
        return true
    }

    // Check whether the orb has reached the final point.
    fun hasReachedGoal(
        ballX: Float, ballY: Float, width: Float, height: Float
    ): Boolean {

        // We need valid Canvas measurements to calculate distance.
        if (width <= 0f || height <= 0f) {
            return false
        }

        // Convert the orb's proportional position to pixels.
        val playerX = ballX * width
        val playerY = ballY * height

        // Convert the final point's position into pixels.
        val goalX = GOAL_X * width
        val goalY = GOAL_Y * height

        // Calculate the difference between both centers.
        val dx = playerX - goalX
        val dy = playerY - goalY

        // Calculate the squared distance between them.
        val distancesSquared = dx * dx + dy * dy

        // Two circles touch when the distance between their center is less than or equal to their combined radius.
        val collisionDistance = BALL_RADIUS + GOAL_RADIUS

        return distancesSquared <= collisionDistance * collisionDistance
    }
}
