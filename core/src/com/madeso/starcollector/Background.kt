package com.madeso.starcollector

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FillViewport

class Background(disposer: Disposer, val scrollspeed : Float)
{
    val batch = SpriteBatch()

    val background_camera = OrthographicCamera()
    val background_display = FillViewport(1f, 1f, background_camera)

    private var backgroundIndex: Int = 0
    private var backgroundTimer = 0.0f

    val SIZE = 1f

    val names = arrayListOf(

            "colored_castle",  "colored_forest",     "uncolored_castle",  "uncolored_forest",  "uncolored_peaks",     "uncolored_plain",
            "colored_desert",  "colored_talltrees",  "uncolored_desert",  "uncolored_hills",   "uncolored_piramids",  "uncolored_talltrees"


    )

    val backgroundsTextures = Array(names.size)
    {
        val text = disposer.CreateTexture("backgrounds/" + names[it] + ".png")
        text.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        text
    }
    val backgroundsSprites = Array(names.size) {
        val sprite = Sprite(backgroundsTextures[it])
        sprite.setPosition(0.0f, 0.0f)
        sprite.setSize(1f, 1f)
        sprite
    }

    val rand = Randomizer()

    fun OnSize(w: Int, h: Int)
    {
        background_display.update(w,h, true)
    }

    fun Draw()
    {
        background_display.apply()
        batch.projectionMatrix = background_camera.combined
        batch.begin()
        drawBackground(batch, backgroundsSprites[backgroundIndex])
        batch.end()
    }

    fun SetRandomBackground()
    {
        backgroundIndex = rand.random(backgroundsSprites.size)
    }

    fun Update(dt: Float) {
        backgroundTimer += dt * scrollspeed
        while (backgroundTimer > 1f) {
            backgroundTimer -= 1f
        }
    }

    private fun drawBackground(batch: SpriteBatch, background: Sprite) {
        background.u = backgroundTimer
        background.u2 = backgroundTimer + 1

        background.v = 0f
        background.v2 = 1f
        background.draw(batch)
    }
}