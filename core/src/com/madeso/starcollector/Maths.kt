package com.madeso.starcollector

import com.badlogic.gdx.math.Vector2


object Maths {

    private val X = (1.0f / Math.sqrt(2.0)).toFloat()
    val LIM = 0.1f

    enum class Direction
    {
        left, right, up, down, center, topleft, topright, bottomleft, bottomright
    }

    fun Classify(dx: Float, dy: Float): Direction {
        if (Math.sqrt((dx * dx + dy * dy).toDouble()) <= LIM) {
            return Direction.center
        }

        // TODO add more directions
        val d = Vector2(dx, dy).nor()

        var r = Direction.right
        var temp = Acos(Vector2(1f, 0f).dot(d))
        var current = temp

        temp = Acos(Vector2(-1f, 0f).dot(d))
        if (temp < current) {
            r = Direction.left
            current = temp
        }

        temp = Acos(Vector2(0f, 1f).dot(d))
        if (temp < current) {
            r = Direction.up
            current = temp
        }

        temp = Acos(Vector2(0f, -1f).dot(d))
        if (temp < current) {
            r = Direction.down
            current = temp
        }

        temp = Acos(Vector2(X, X).dot(d))
        if (temp < current) {
            r = Direction.topright
            current = temp
        }

        temp = Acos(Vector2(-X, X).dot(d))
        if (temp < current) {
            r = Direction.topleft
            current = temp
        }

        temp = Acos(Vector2(X, -X).dot(d))
        if (temp < current) {
            r = Direction.bottomright
            current = temp
        }

        temp = Acos(Vector2(-X, -X).dot(d))
        if (temp < current) {
            r = Direction.bottomleft
            current = temp
        }

        return r
    }

    private fun Acos(dot: Float): Float {
        return Math.acos(dot.toDouble()).toFloat()
    }

}