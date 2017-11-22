package com.madeso.starcollector

class SolutionMap(val width: Int, val height: Int)
{
    // 1-15 of bitcombined from paths
    val world = World<Int>(width, height)

    fun Clear()
    {
        world.Clear()
    }

    enum class Placement
    {
        Left, Right, Top, Bottom
    }

    fun ShiftAmmount(pl: Placement) = when(pl)
    {
        Placement.Top -> 3
        Placement.Right -> 2
        Placement.Bottom -> 1
        Placement.Left -> 0
    }

    fun HasValueAt(x: Int, y: Int) = world.GetStarIndex(x, y) != null
    fun ValueAt(x: Int, y: Int) = world.GetStarIndex(x, y) ?: 0

    fun PlaceAt(lastx: Int, lasty: Int, pl : Placement)
    {
        val current = ValueAt(lastx, lasty)
        val shift = ShiftAmmount(pl)
        val shift_amm = 1 shl shift
        val new = current or shift_amm
        // between 1-15 inclusive
        assert(new > 0)
        assert(new < 16)
        // println("Placing path %s %d->%d (%d %d)".format(pl.toString(), current, new, shift, shift_amm))
        world.PlaceStar(lastx, lasty, new)

    }

    fun PlaceAt(lastx: Int, lasty: Int, dx: Int, dy: Int)
    {
        // print(String.format("(%d %d) (%d %d): ", lastx, lasty, dx, dy))
        if(dx <0)
        {
            PlaceAt(lastx, lasty, Placement.Right)
        }
        else if(dx > 0)
        {
            PlaceAt(lastx, lasty, Placement.Left)
        }
        else if(dy > 0)
        {
            PlaceAt(lastx, lasty, Placement.Bottom)
        }
        else if(dy < 0)
        {
            PlaceAt(lastx, lasty, Placement.Top)
        }
        else
        {
            print("Invalid direction when placing")
        }
    }

    fun AddTilePath(x: Int, y: Int, dx: Int, dy: Int)
    {
        // when moving horizontal this should place a marker in a pattern like this:
        // <-- | -->
        PlaceAt(x+dx, y+dy, dx, dy)
        PlaceAt(x, y, dx*-1, dy*-1)
    }
}