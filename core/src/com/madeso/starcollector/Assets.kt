package com.madeso.starcollector

import com.badlogic.gdx.audio.Sound

class Assets(disposer: Disposer)
{
    val sScore = disposer.CreateSound("data/score.wav")
    val sStep = disposer.CreateSound("data/step.wav")
    val sDie = disposer.CreateSound("data/die.wav")
}