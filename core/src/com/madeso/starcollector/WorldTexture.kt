package com.madeso.starcollector

class WorldTexture(disposer: Disposer, name: String, SIZE: Float)
{
    val sprites = Array(9)
    {
        disposer.CreateSpriteRatio(disposer.CreateTexture("world/" + name + "/" + (it+1).toString() + ".png"), SIZE)
    }

    fun sprite(x: Int, y: Int) = sprites[y*3 + x]
}