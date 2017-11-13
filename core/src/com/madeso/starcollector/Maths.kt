package com.madeso.starcollector

import com.badlogic.gdx.math.Vector2


object Maths {

    private val X = (1.0f / Math.sqrt(2.0)).toFloat()
    val LIM = 0.1f

    fun Classify(dx: Float, dy: Float): Int {
        if (Math.sqrt((dx * dx + dy * dy).toDouble()) <= LIM) {
            return 5
        }

        // TODO add more directions
        val d = Vector2(dx, dy).nor()

        var r = 6
        var temp = Acos(Vector2(1f, 0f).dot(d))
        var current = temp

        temp = Acos(Vector2(-1f, 0f).dot(d))
        if (temp < current) {
            r = 4
            current = temp
        }

        temp = Acos(Vector2(0f, 1f).dot(d))
        if (temp < current) {
            r = 8
            current = temp
        }

        temp = Acos(Vector2(0f, -1f).dot(d))
        if (temp < current) {
            r = 2
            current = temp
        }

        temp = Acos(Vector2(X, X).dot(d))
        if (temp < current) {
            r = 9
            current = temp
        }

        temp = Acos(Vector2(-X, X).dot(d))
        if (temp < current) {
            r = 7
            current = temp
        }

        temp = Acos(Vector2(X, -X).dot(d))
        if (temp < current) {
            r = 3
            current = temp
        }

        temp = Acos(Vector2(-X, -X).dot(d))
        if (temp < current) {
            r = 1
            current = temp
        }

        return r
    }

    private fun Acos(dot: Float): Float {
        return Math.acos(dot.toDouble()).toFloat()
    }

}