package com.madeso.starcollector

class World<T>(val width : Int, val height: Int)
{
    class Capture<T>(val t : T)
    {
    }

    private var world = Array(width) { Array<Capture<T>?>(height){null} }

    fun IsFree(nx: Int, ny: Int) = world[nx][ny] == null

    fun GetStarIndex(nx: Int, ny: Int) : T? {
        val r = world[nx][ny] ?: return null
        return r.t
    }

    fun PlaceStar(x: Int, y: Int, star_index : T) {
        world[x][y] = Capture(star_index)
    }

    fun RemoveStar(x: Int, y: Int) {
        world[x][y] = null
    }

    fun Clear()
    {
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                world[x][y] = null
            }
        }
    }
}