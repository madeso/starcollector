package com.madeso.starcollector

class WorldTexture(disposer: Disposer, name: String, SIZE: Float)
{
    val left = disposer.CreateSpriteRatio(disposer.CreateTexture("world/" + name + "Left.png"), SIZE)
    val mid = disposer.CreateSpriteRatio(disposer.CreateTexture("world/" + name + "Mid.png"), SIZE)
    val right = disposer.CreateSpriteRatio(disposer.CreateTexture("world/" + name + "Right.png"), SIZE)
}