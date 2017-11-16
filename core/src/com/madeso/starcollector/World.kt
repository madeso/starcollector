package com.madeso.starcollector

class World(val width : Int, val height: Int)
{
    private var world = Array(width) { IntArray(height) }

    fun IsFree(nx: Int, ny: Int) = world[nx][ny] == 0

    fun PlaceStar(x: Int, y: Int) {
        world[x][y] = 1
    }

    fun RemoveStar(x: Int, y: Int) {
        world[x][y] = 0
    }

    fun Clear()
    {
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                world[x][y] = 0
            }
        }
    }
}