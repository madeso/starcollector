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

    val backgroundsTexture = disposer.CreateTexture("data/backgrounds.png")
    val backgroundsSprite = Sprite(backgroundsTexture)

    private var backgroundIndex: Int = 0
    private var backgroundTimer = 0.0f

    val rand = Randomizer()

    init
    {
        backgroundsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        backgroundsSprite.setPosition(0.0f, 0.0f)
        backgroundsSprite.setSize(1f, 1f)
    }

    fun OnSize(w: Int, h: Int)
    {
        background_display.update(w,h, true)
    }

    fun Draw()
    {
        background_display.apply()
        batch.projectionMatrix = background_camera.combined
        batch.begin()
        drawBackground(batch, backgroundsSprite)
        batch.end()
    }

    fun SetRandomBackground()
    {
        backgroundIndex = rand.random(3)
    }

    fun Update(dt: Float) {
        backgroundTimer += dt * scrollspeed
        while (backgroundTimer > 1.0f) {
            backgroundTimer -= 1.0f
        }
    }

    private fun drawBackground(batch: SpriteBatch, background: Sprite) {
        background.u = backgroundTimer
        background.u2 = backgroundTimer + 1

        val HEIGHT = 1 / 3.0f
        background.v = backgroundIndex * HEIGHT
        background.v2 = (backgroundIndex + 1) * HEIGHT
        background.draw(batch)
    }
}