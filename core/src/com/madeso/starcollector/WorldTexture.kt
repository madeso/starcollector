package com.madeso.starcollector

class WorldTexture(disposer: Disposer, name: String, SIZE: Float)
{
    val mid = disposer.CreateSpriteRatio(disposer.CreateTexture("world/" + name + "Mid.png"), SIZE)
}