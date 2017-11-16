package com.madeso.starcollector

import java.util.*

class Randomizer
{
    private val rand = Random()

    fun random(size: Int): Int {
        return rand.nextInt(size)
    }
}