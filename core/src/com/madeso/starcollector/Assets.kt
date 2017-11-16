package com.madeso.starcollector

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.BitmapFont

class Assets(disposer: Disposer)
{
    val sScore = disposer.CreateSound("data/score.wav")
    val sStep = disposer.CreateSound("data/step.wav")
    val sDie = disposer.CreateSound("data/die.wav")

    val music = disposer.CreateMusic("data/Malloga_Ballinga_Mastered_mp_0.mp3")
    val font = BitmapFont() // BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);
}