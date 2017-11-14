package com.madeso.starcollector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Disposer
{
    fun CreateSprite(texture: Texture, size: Float): Sprite
    {
        val sprite = Sprite(TextureRegion(texture))
        sprite.setSize(size, size)
        sprite.setOrigin(sprite.width / 2,
                sprite.height / 2)
        return sprite
    }

    fun CreateTexture(path: String): Texture
    {
        val texture = Texture(Gdx.files.internal(path))
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        return texture
    }

    fun CreateSound(path: String) : Sound
    {
        return Gdx.audio.newSound(Gdx.files.internal(path))
    }

    fun CreateMusic(path: String) : Music
    {
        return Gdx.audio.newMusic(Gdx.files.internal(path))
    }

    fun DisposeAll()
    {
        // todo: implement me
    }
}