package com.madeso.starcollector

import java.util.*

class WorldGenerator(val world: World, val width: Int, val height: Int, val items: Int)
{
    var playerx: Int = 0
    var playery: Int = 0

    private var solution = ArrayList<Vec2i>()
    private var mem = Array(width) { BooleanArray(height) }

    private val rand = Randomizer()

    private fun listValidPositions(sx: Int, sy: Int): List<Vec2i> {
        val r = ArrayList<Vec2i>()

        for (x in 0..width - 1) {
            if (x != sx) {
                r.add(Vec2i(x, sy))
            }
        }

        for (y in 0..height - 1) {
            if (y != sy) {
                r.add(Vec2i(sx, y))
            }
        }

        return r
    }

    private fun dogenworld(number_of_stars: Int): Boolean {
        solution.clear()

        playerx = rand.random(width)
        playery = rand.random(height)

        world.Clear()
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                mem[x][y] = false
            }
        }

        return fillworld(number_of_stars)
    }

    private fun fillworld(number_of_stars: Int): Boolean {
        var x = playerx
        var y = playery
        var nx: Int
        var ny: Int

        var done = false

        var positions: List<Vec2i> = ArrayList()
        var pos: Vec2i? = null
        var generate = true
        var randomindex = 0
        var i = 0

        do {
            if (generate) {
                positions = listValidPositions(x, y)
                // generated valid positions
                generate = false
            }

            if (positions.size == 0) {
                // no more valid positions, failing...
                return false
            }

            randomindex = rand.random(positions.size)
            pos = positions[randomindex]
            nx = pos.x
            ny = pos.y

            if (world.IsFree(nx, ny) && isfree(nx, ny)) {
                remember(x, y, nx, ny)
                x = nx
                y = ny
                world.PlaceStar(x, y, rand.random(number_of_stars))
                // itemsleft = itemsleft + 1

                i = i + 1
                if (i >= items) {
                    done = true
                }

                generate = true
            }
        } while (done == false)

        // for solution traversing
        solution.add(Vec2i(x, y))

        return true
    }

    fun genworld(number_of_stars: Int) {
        var complete = false
        do {
            complete = dogenworld(number_of_stars)
        } while (complete == false)
    }

    fun isfree(x: Int, y: Int): Boolean {
        return if (playerx == x && playery == y) {
            false
        } else mem[x][y] == false
    }

    internal fun remember(sx: Int, sy: Int, nx: Int, ny: Int) {
        var x = sx
        var y = sy
        var dx = 0
        var dy = 0

        if (x > nx) {
            dx = -1
        } else if (x < nx) {
            dx = 1
        }

        if (y > ny) {
            dy = -1
        } else if (y < ny) {
            dy = 1
        }

        do {
            mem[x][y] = true

            // for solution traversing
            solution.add(Vec2i(x, y))

            x = x + dx
            y = y + dy
        } while (false == (x == nx && y == ny))
    }

}